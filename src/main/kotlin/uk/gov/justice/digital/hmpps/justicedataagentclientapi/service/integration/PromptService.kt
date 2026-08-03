package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration

import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.PromptRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response.PromptsResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.PromptResponse

interface PromptService {
  suspend fun savePrompt(promptRequest: PromptRequest): PromptResponse

  suspend fun updatePrompt(key: String, promptRequest: PromptRequest): PromptResponse

  suspend fun getPrompts(): List<PromptsResponse> // return all prompts

  suspend fun getPromptsByKeyAndVersion(key: String, version: Int): PromptResponse //

  suspend fun getPromptByKey(key: String): PromptResponse // return prompt with latest version

  suspend fun deletePromptByKey(key: String)
}
