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
) : JdaMessagePublisher {

  @Autowired
  private lateinit var hmppsQueueService: HmppsQueueService
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }

  override suspend fun publishJdaRequest(jdaRequest: JdaRequest) {
    logger.info("Sending jda request message to queue: $jdaRequestQueueName with correlation id: ${jdaRequest.correlationId}")
    val sqsTemplate =
      SqsTemplate
        .newTemplate(
          hmppsQueueService
            .findByQueueId("jdarequestqueues")!!.sqsClient,
        )
    sqsTemplate.send { to -> to.queue(jdaRequestQueueName).payload(jdaRequest) }
    logger.info("Jda request message sent to queue: $jdaRequestQueueName with correlation id: ${jdaRequest.correlationId}")
  }
}
