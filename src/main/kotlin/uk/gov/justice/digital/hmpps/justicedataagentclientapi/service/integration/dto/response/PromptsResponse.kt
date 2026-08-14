package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response

import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.PromptVersionResponse
import java.time.LocalDateTime
import java.util.UUID

data class PromptsResponse(
  var id: UUID,
  val promptKey: String,
  val description: String,
  val isDeleted: Boolean,
  val createdBy: UUID,
  val createdDate: LocalDateTime,
  val promptVersions: List<PromptVersionResponse>,
)
