package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.client.JdaWorkerClient
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.PromptRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response.PromptsResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.PromptResponse
import java.util.UUID

@Component
class PromptServiceImpl(private val jdaWorkerClient: JdaWorkerClient) : PromptService {
  override suspend fun savePrompt(promptRequest: PromptRequest): PromptResponse {
    return jdaWorkerClient.createPrompt(promptRequest)
  }

  override suspend fun updatePrompt(
    key: String,
    promptRequest: PromptRequest,
  ): PromptResponse {
    TODO("Not yet implemented")
  }

  override suspend fun getPrompts(): List<PromptsResponse> {
    return jdaWorkerClient.getPrompts()
  }

  override suspend fun getPromptsByKeyAndVersion(
    key: String,
    version: Int,
  ): PromptResponse {
    return jdaWorkerClient.getPromptByKeyAndVersion(key, version)
  }

  override suspend fun getPromptByKey(key: String): PromptResponse {
    return jdaWorkerClient.getPromptByKey(key)
  }

  override suspend fun deletePromptByKey(key: String) {
    jdaWorkerClient.deletePromptByKey(key)
  }

}