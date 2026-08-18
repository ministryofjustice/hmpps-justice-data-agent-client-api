package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event.JdaMessagePublisher
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.client.JdaWorkerClient
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.exception.NotFoundException
import uk.gov.justice.hmpps.sqs.HmppsQueueService

@Service
class JdaWorkerServiceImpl(
  private val jdaWorkerClient: JdaWorkerClient,
  private val objectMapper: ObjectMapper,
  @param:Value("\${hmpps.sqs.queues.jdarequestqueus.queuename}") val jdaRequestQueueName: String,
  @param:Value("\${hmpps.sqs.queues.jdaresponsequeus.queuename}") val jdaResponseQueueName: String,
) : JdaWorkerService {
  @Autowired
  private lateinit var jdaMessagePublisher: JdaMessagePublisher
  @Autowired
  private lateinit var hmppsQueueService: HmppsQueueService
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }
  override suspend fun submitSynchronousRequest(jdaRequest: JdaRequest): JdaResponse = jdaWorkerClient.submitSynchronousRequest(jdaRequest)

  override suspend fun submitAsynchronousRequest(jdaRequest: JdaRequest) {
    logger.info("Send jda async request to the jda request queue")
    jdaMessagePublisher.publishJdaRequest(jdaRequest)
  }

  override suspend fun dequeueResponse(): JdaResponse {
    try {
      logger.info("Dequeue  response")
      val sqsClient = hmppsQueueService
        .findByQueueId("jdaresponsequeus")!!.sqsClient
      val queueUrl = sqsClient.getQueueUrl(
        GetQueueUrlRequest.builder()
          .queueName(jdaResponseQueueName)
          .build(),
      )?.join()?.queueUrl()
      val messages = sqsClient.receiveMessage(
        ReceiveMessageRequest.builder()
          .maxNumberOfMessages(1)
          .queueUrl(queueUrl)
          .build(),
      )?.join()
      if (messages?.hasMessages() == true) {
        val jdaResponse = objectMapper.readValue(messages?.messages()[0]?.body(), JdaResponse::class.java)
        logger.info("Deleting message from the jda response queue")
        sqsClient.deleteMessage(
          DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(messages?.messages()[0]?.receiptHandle())
            .build(),
        )
        logger.info("returning dequeued response")
        return jdaResponse
      }
      throw NotFoundException("Queue is empty, no message in queue to dequeue")
    } catch (e: Exception) {
      logger.error("Error during dequeue response, queue is already empty", e)
      throw NotFoundException("Queue is empty, no message in queue to dequeue")
    }

  }
}
