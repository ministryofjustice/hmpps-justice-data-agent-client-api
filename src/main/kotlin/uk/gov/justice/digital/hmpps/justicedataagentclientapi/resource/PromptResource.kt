package uk.gov.justice.digital.hmpps.justicedataagentclientapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.PromptService
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.PromptRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response.PromptsResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.PromptResponse
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@RequestMapping(value = ["/v1"])
class PromptResource(
  private val promptService: PromptService,
) {
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }

  @Tag(name = "prompts")
  @Operation(
    summary = "Add a new prompt.",
    description = "This api endpoint is for adding new Prompt.  Requires role ROLE_JUSTICE_DATA_AGENT_PROMPTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_PROMPTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden user do not required role.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PostMapping("/prompts")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun createPrompt(@RequestBody promptRequest: PromptRequest): ResponseEntity<PromptResponse> {
    val prompt = promptService.savePrompt(promptRequest)
    logger.info("New Prompt created for prompt key: ${promptRequest.promptKey}")
    return ResponseEntity.status(HttpStatus.CREATED).body(prompt)
  }

  @Tag(name = "prompts")
  @Operation(
    summary = "Update a prompt by key",
    description = "This api endpoint is for adding new version of a existing prompt.  Requires role ROLE_JUSTICE_DATA_AGENT_PROMPTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_PROMPTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden user do not required role.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PutMapping("/prompts/{key}")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun updatePrompt(
    @PathVariable key: String,
    @RequestBody promptRequest: PromptRequest,
  ): ResponseEntity<PromptResponse> {
    val prompt = promptService.updatePrompt(key, promptRequest)
    logger.info("Prompt updated for prompt key: $key")
    return ResponseEntity.status(HttpStatus.OK).body(prompt)
  }

  @Tag(name = "prompts")
  @Operation(
    summary = "Get prompts",
    description = "This api endpoint is for get all prompts with versions.  Requires role ROLE_JUSTICE_DATA_AGENT_PROMPTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_PROMPTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden user do not required role.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @GetMapping("/prompts")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun getPrompts(): ResponseEntity<List<PromptsResponse>> {
    val prompt = promptService.getPrompts()
    logger.info("Returning all prompts with versions")
    return ResponseEntity.status(HttpStatus.OK).body(prompt)
  }

  @Tag(name = "prompts")
  @Operation(
    summary = "Get prompt by key",
    description = "This api endpoint is for getting prompt with latest version by key.  Requires role ROLE_JUSTICE_DATA_AGENT_PROMPTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_PROMPTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden user do not required role.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @GetMapping("/prompts/{key}")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun getPromptByKey(@PathVariable key: String): ResponseEntity<PromptResponse> {
    val prompt = promptService.getPromptByKey(key)
    logger.info("Returning prompt for a given key: $key ")
    return ResponseEntity.status(HttpStatus.OK).body(prompt)
  }

  @Tag(name = "prompts")
  @Operation(
    summary = "Get a prompt by key and version",
    description = "This api endpoint is for getting prompt by key and version.  Requires role ROLE_JUSTICE_DATA_AGENT_PROMPTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_PROMPTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden user do not required role.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @GetMapping("/prompts/{key}/versions/{version}")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun getPromptByKeyAndVersion(@PathVariable key: String, @PathVariable version: Int): ResponseEntity<PromptResponse> {
    val prompt = promptService.getPromptsByKeyAndVersion(key, version)
    logger.info("Returning prompt for a given key: $key and version: $version")
    return ResponseEntity.status(HttpStatus.OK).body(prompt)
  }

  @Tag(name = "prompts")
  @Operation(
    summary = "Delete a prompt by key",
    description = "This api endpoint delete prompt by key.  Requires role ROLE_JUSTICE_DATA_AGENT_PROMPTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_PROMPTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden user do not required role.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @DeleteMapping("/prompts/{key}")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_PROMPTS')")
  suspend fun deletePromptByKey(@PathVariable key: String): ResponseEntity<Void> {
    promptService.deletePromptByKey(key)
    logger.info("Prompt with key: $key has been deleted")
    return ResponseEntity.status(HttpStatus.OK).build()
  }
}
