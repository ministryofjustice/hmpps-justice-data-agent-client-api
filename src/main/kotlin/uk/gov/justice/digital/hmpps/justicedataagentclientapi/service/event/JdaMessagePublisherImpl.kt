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
  @param:Value("\${hmpps.sqs.queues.jdarequestqueus.queuename}") private val jdaRequestQueueName: String,
) : JdaMessagePublisher {

  @Autowired
  private lateinit var hmppsQueueService: HmppsQueueService
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }

  override suspend fun publishJdaRequest(jdaRequest: JdaRequest) {
    logger.info("Sending jda request message to queue")
    val sqsTemplate =
      SqsTemplate
        .newTemplate(
          hmppsQueueService
            .findByQueueId("jdarequestqueus")!!.sqsClient,
        )
    sqsTemplate.send { to -> to.queue(jdaRequestQueueName).payload(jdaRequest) }
  }

  /*override fun publishJdaResponse(jdaResponse: JdaResponse) {
    val sqsTemplate =
      SqsTemplate
        .newTemplate(
          hmppsQueueService
            .findByQueueId("jdaresponsequeus")!!.sqsClient,
        )
    logger.info("Sending jda response message to queue")
    sqsTemplate.send { to -> to.queue("sqs_response_queue_name").payload(jdaResponse) }
  }*/
}
