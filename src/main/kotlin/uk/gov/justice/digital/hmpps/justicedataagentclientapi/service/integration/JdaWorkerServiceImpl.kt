package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.exception.SqsQueueException
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
  @param:Value("\${hmpps.sqs.queues.jdarequestqueues.queuename}") val jdaRequestQueueName: String,
  @param:Value("\${hmpps.sqs.queues.jdaresponsequeues.queuename}") val jdaResponseQueueName: String,
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
    logger.info("Send async jda request to the jda request queue")
    jdaMessagePublisher.publishJdaRequest(jdaRequest)
  }

  override suspend fun dequeueResponse(): JdaResponse {
    try {
      logger.info("Dequeue jda response queue: $jdaRequestQueueName")
      val responseQueue = hmppsQueueService
        .findByQueueId("jdaresponsequeues")
      val sqsClient = responseQueue?.sqsClient
      val queueUrl = responseQueue?.queueUrl
      val messages = sqsClient?.receiveMessage(
        ReceiveMessageRequest.builder()
          .maxNumberOfMessages(1)
          .queueUrl(queueUrl)
          .build(),
      )?.join()
      if (messages?.hasMessages() == true) {
        val jdaResponse = objectMapper.readValue(messages.messages()[0]?.body(), JdaResponse::class.java)
        logger.info("Deleting message from the jda response queue: $jdaRequestQueueName with correlation id: ${jdaResponse.correlationId}")
        sqsClient.deleteMessage(
          DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(messages.messages()[0]?.receiptHandle())
            .build(),
        )
        logger.info("returning dequeued jda response with correlation id: ${jdaResponse.correlationId}")
        return jdaResponse
      }
      throw NotFoundException("Queue is empty, no message in queue.")
    } catch (e: Exception) {
      logger.error("Error during dequeue response : ${e.message}")
      if (e is NotFoundException) {
        throw NotFoundException("Queue is empty, no message in queue to dequeue.") // http response code for this will be 404
      }
      val message = "Unexpected Exception during dequeue response queue: ${e.message}" // http response code for this will be 500
      throw SqsQueueException(message)
    }
  }
}
