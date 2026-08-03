package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request

import java.util.UUID

data class PromptVersionRequest(
  val llmModel: String,
  val promptTemplate: String,
  val requestContract: String,
  val responseContract: String? = null,
)
