package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.hmpps.sqs.HmppsQueueService

@Component
class JdaMessagePublisherImpl(
  @param:Value("\${hmpps.sqs.queues.jdarequestqueues.queuename}") private val jdaRequestQueueName: String,
  @param:Value("\${hmpps.sqs.queues.jdarequestqueues.dlqName}") private val jdaRequestDlqName: String,
) : JdaMessagePublisher {

  @Autowired
  private lateinit var hmppsQueueService: HmppsQueueService
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }

  override suspend fun publishJdaRequest(jdaRequest: JdaRequest) {
    val awsSqsClient = hmppsQueueService
      .findByQueueId("jdarequestqueues")!!.sqsClient
    val sqsTemplate =
      SqsTemplate
        .newTemplate(
          awsSqsClient,
        )
    try {
      logger.info("Sending jda request message to queue: $jdaRequestQueueName with correlation id: ${jdaRequest.correlationId}")
      sqsTemplate.send { to -> to.queue(jdaRequestQueueName).payload(jdaRequest) }
      logger.info("Jda request message sent to queue: $jdaRequestQueueName with correlation id: ${jdaRequest.correlationId}")
    } catch (e: Exception) {
      logger.error("Exception occurred when sending message to queue: $jdaRequestQueueName with correlation id: ${jdaRequest.correlationId},  exception: ${e.message}")
      logger.warn("Sending jda request message with correlation id: ${jdaRequest.correlationId} to dlq name: $jdaRequestDlqName")
      sqsTemplate.send { to -> to.queue(jdaRequestDlqName).payload(jdaRequest) }
    }
  }
}
