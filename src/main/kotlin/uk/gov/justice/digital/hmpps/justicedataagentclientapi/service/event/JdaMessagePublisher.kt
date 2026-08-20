package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event

import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest

interface JdaMessagePublisher {

  suspend fun publishJdaRequest(jdaRequest: JdaRequest)
}
