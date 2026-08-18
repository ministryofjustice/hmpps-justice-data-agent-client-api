package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse
import uk.gov.justice.hmpps.sqs.HmppsQueueService

@Component
class JdaMessagePublisherImpl(
  private val hmppsQueueService: HmppsQueueService,
) : JdaMessagePublisher {

  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }

  override fun publishJdaRequest(jdaRequest: JdaRequest) {
    val sqsTemplate =
      SqsTemplate
        .newTemplate(
          hmppsQueueService
            .findByQueueId("jdarequestqueus")!!.sqsClient,
        )
    logger.info("Sending jda request message to queue")
    sqsTemplate.send { to -> to.queue("sqs_request_queue_name").payload(jdaRequest) }
  }

  override fun publishJdaResponse(jdaResponse: JdaResponse) {
    val sqsTemplate =
      SqsTemplate
        .newTemplate(
          hmppsQueueService
            .findByQueueId("jdaresponsequeus")!!.sqsClient,
        )
    logger.info("Sending jda response message to queue")
    sqsTemplate.send { to -> to.queue("sqs_response_queue_name").payload(jdaResponse) }
  }
}
