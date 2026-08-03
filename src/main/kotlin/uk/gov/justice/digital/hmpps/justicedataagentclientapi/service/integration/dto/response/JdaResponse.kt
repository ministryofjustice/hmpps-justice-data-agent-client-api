package uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response

import java.util.UUID

data class JdaResponse(
  val requestId: UUID,
  val correlationId: UUID,
  val prompt: Prompt,
  val responseData: Any,
)
