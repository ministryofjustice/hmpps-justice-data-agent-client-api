package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request

import java.util.*

data class JdaRequest(
  val correlationId: UUID,
  val prompt: Prompt,
  val requestData: Any,
)

data class Prompt(
  val key: String,
  val version: Int,
)
