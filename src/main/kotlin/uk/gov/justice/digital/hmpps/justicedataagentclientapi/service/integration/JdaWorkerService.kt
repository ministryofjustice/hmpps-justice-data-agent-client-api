package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration

import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse

interface JdaWorkerService {

  suspend fun submitSynchronousRequest(jdaRequest: JdaRequest): JdaResponse

  suspend fun submitNonBlockingRequest(jdaRequest: JdaRequest): JdaResponse
}
