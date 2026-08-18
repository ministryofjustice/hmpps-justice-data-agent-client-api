package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event

import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse

interface JdaMessageListener {
  // fun onJdaRequestMessageReceived(message: JdaRequest)

  fun onJdaResponseMessageReceived(message: JdaResponse)
}
