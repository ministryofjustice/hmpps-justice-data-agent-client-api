package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse

@Component
class JdaMessageListenerImpl(
  private val jdaMessagePublisher: JdaMessagePublisher,
) : JdaMessageListener {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  /*@SqsListener("jdarequestqueus", factory = "hmppsQueueContainerFactoryProxy")
  override  fun onJdaRequestMessageReceived(message: JdaRequest) {
    logger.info("Sqs jda request message received")
    runBlocking {
      jdaWorkerService.handleSynchronousRequest(message)
    }
  }*/

  @SqsListener("jdaresponsequeus", factory = "hmppsQueueContainerFactoryProxy")
  override fun onJdaResponseMessageReceived(jdaResponse: JdaResponse) {
    jdaMessagePublisher.publishJdaResponse(jdaResponse)
  }
}
