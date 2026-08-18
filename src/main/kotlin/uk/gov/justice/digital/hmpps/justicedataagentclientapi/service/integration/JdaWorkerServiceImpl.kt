package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration

import org.slf4j.LoggerFactory
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
import uk.gov.justice.hmpps.sqs.HmppsQueueService

@Service
class JdaWorkerServiceImpl(
  private val jdaWorkerClient: JdaWorkerClient,
  private val jdaMessagePublisher: JdaMessagePublisher,
  private val hmppsQueueService: HmppsQueueService,
  private val objectMapper: ObjectMapper,
  @param:Value("\${hmpps.sqs.queues.jdarequestqueus.queuename}") val queueName: String,
) : JdaWorkerService {
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }
  override suspend fun submitSynchronousRequest(jdaRequest: JdaRequest): JdaResponse = jdaWorkerClient.submitSynchronousRequest(jdaRequest)

  override suspend fun submitAsynchronousRequest(jdaRequest: JdaRequest) {
    logger.info("Send jda async request to the jda request queue")
    jdaMessagePublisher.publishJdaRequest(jdaRequest)
  }

  override suspend fun dequeueResponse(): JdaResponse {
    logger.info("Dequeue  response")
    val sqsClient = hmppsQueueService
      .findByQueueId("jdarequestqueus")!!.sqsClient
    val messages = sqsClient.receiveMessage(
      ReceiveMessageRequest.builder()
        .maxNumberOfMessages(1)
        .queueUrl(
          sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder()
              .queueName("")
              .build(),
          ).resultNow().queueUrl(),
        )
        .build(),
    )
    sqsClient.getQueueUrl(
      GetQueueUrlRequest.builder()
        .queueName("")
        .build(),
    ).resultNow().queueUrl()
    val jdaResponse = objectMapper.readValue(messages.resultNow().messages()[0].body(), JdaResponse::class.java)
    val message = messages.resultNow().messages()[0]
    // Dequeue message
    sqsClient.deleteMessage(
      DeleteMessageRequest.builder()
        .queueUrl(
          sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder()
              .queueName(queueName)
              .build(),
          ).resultNow().queueUrl(),
        )
        .receiptHandle(message.receiptHandle())
        .build(),
    )
    logger.info("returning dequeued response")
    return jdaResponse
  }
}
