package uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response

import tools.jackson.databind.JsonNode
import java.time.LocalDateTime
import java.util.UUID

data class PromptVersionResponse(
  var id: UUID,
  val version: Int,
  val llmModel: String,
  val promptTemplate: String,
  val requestContract: JsonNode,
  val responseContract: JsonNode? = null,
  val createdBy: UUID,
  val createdDate: LocalDateTime,
)
