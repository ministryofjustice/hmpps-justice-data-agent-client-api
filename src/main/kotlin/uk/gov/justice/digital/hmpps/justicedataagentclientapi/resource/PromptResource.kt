package uk.gov.justice.digital.hmpps.justicedataagentclientapi.resource

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.PromptService
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.PromptRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response.PromptsResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.PromptResponse

@RestController
@RequestMapping(value = ["/v1"])
class PromptResource(
  private val promptService: PromptService,
) {

  @PostMapping("/prompts")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun createPrompt(@RequestBody promptRequest: PromptRequest): ResponseEntity<PromptResponse> {
    val prompt = promptService.savePrompt(promptRequest)
    return ResponseEntity.status(HttpStatus.CREATED).body(prompt)
  }

  @GetMapping("/prompts")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun getPrompts(): ResponseEntity<List<PromptsResponse>> {
    val prompt = promptService.getPrompts()
    return ResponseEntity.status(HttpStatus.OK).body(prompt)
  }

  @GetMapping("/prompts/{key}")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun getPromptByKey(@PathVariable key: String): ResponseEntity<PromptResponse> {
    val prompt = promptService.getPromptByKey(key)
    return ResponseEntity.status(HttpStatus.OK).body(prompt)
  }

  @GetMapping("/prompts/{key}/versions/{version}")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun getPromptByKeyAndVersion(@PathVariable key: String, @PathVariable version: Int): ResponseEntity<PromptResponse> {
    val prompt = promptService.getPromptsByKeyAndVersion(key, version)
    return ResponseEntity.status(HttpStatus.OK).body(prompt)
  }

  @DeleteMapping("/prompts/{key}/versions/{version}")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun deletePromptByKey(@PathVariable key: String): ResponseEntity<Void> {
    promptService.deletePromptByKey(key)
    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
