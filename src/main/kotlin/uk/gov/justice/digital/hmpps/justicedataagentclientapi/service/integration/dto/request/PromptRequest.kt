package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request

import java.util.UUID

data class PromptRequest(
  val promptKey: String,
  val description: String,
  val createdBy: UUID,
  val promptVersion: PromptVersionRequest,
)
