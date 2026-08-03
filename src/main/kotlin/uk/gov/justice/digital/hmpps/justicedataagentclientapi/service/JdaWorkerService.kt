package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service

interface JdaWorkerService {
  suspend fun sendSynchronousRequest()

  suspend fun createPrompt()

  suspend fun createPromptVersion()

  suspend fun getPromptById()

  suspend fun getPromptVersionById()
}
