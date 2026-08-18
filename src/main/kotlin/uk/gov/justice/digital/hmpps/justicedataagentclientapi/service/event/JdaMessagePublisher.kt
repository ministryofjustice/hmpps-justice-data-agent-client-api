package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event

import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse

interface JdaMessagePublisher {
  fun publishJdaRequest(jdaRequest: JdaRequest)

  fun publishJdaResponse(jdaResponse: JdaResponse)
}
