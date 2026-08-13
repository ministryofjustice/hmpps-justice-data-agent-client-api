package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request

import tools.jackson.databind.JsonNode

data class PromptVersionRequest(
  val llmModel: String,
  val promptTemplate: String,
  val requestContract: JsonNode,
  val responseContract: JsonNode? = null,
)
