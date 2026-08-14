package uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response

import java.time.LocalDateTime
import java.util.UUID

data class PromptResponse(
  var id: UUID,
  val promptKey: String,
  val description: String,
  val isDeleted: Boolean,
  val createdBy: UUID,
  val createdDate: LocalDateTime,
  val promptVersion: PromptVersionResponse,
)
