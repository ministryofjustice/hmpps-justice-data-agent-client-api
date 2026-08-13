package uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response

import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.Prompt
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response.MetaData
import java.util.UUID

data class JdaResponse(
  val requestId: UUID,
  val correlationId: UUID,
  val prompt: Prompt,
  val responseData: Any,
  val metaData: MetaData,
)
