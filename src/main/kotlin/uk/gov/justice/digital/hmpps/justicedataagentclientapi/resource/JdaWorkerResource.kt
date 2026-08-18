package uk.gov.justice.digital.hmpps.justicedataagentclientapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.JdaWorkerService
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@RequestMapping(value = ["v1"])
class JdaWorkerResource(private val jdaWorkerService: JdaWorkerService) {

  @Tag(name = "Jda requests")
  @Operation(
    summary = "Synchronous request to jda worker.",
    description = "This api endpoint is for sending synchronous request  to jda worker.  Requires role ROLE_JUSTICE_DATA_AGENT_REQUESTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_REQUESTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden to access this endpoint. The issue can be logged staff and prisoner have different establishment.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PostMapping("submitrequest")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_REQUESTS')")
  suspend fun submitSynchronousRequest(@RequestBody jdaRequest: JdaRequest): ResponseEntity<JdaResponse> {
    val response = jdaWorkerService.submitSynchronousRequest(jdaRequest)
    return ResponseEntity(response, HttpStatus.OK)
  }

  @Tag(name = "Jda requests")
  @Operation(
    summary = "Asynchronous request to jda worker.",
    description = "This api endpoint is for sending asynchronous request  to jda worker.  Requires role ROLE_JUSTICE_DATA_AGENT_REQUESTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_REQUESTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden to access this endpoint. The issue can be logged staff and prisoner have different establishment.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PostMapping("queuerequest")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_REQUESTS')")
  suspend fun submitAsynchronousRequest(@RequestBody jdaRequest: JdaRequest): ResponseEntity<Void> {
    jdaWorkerService.submitAsynchronousRequest(jdaRequest)
    return ResponseEntity.status(HttpStatus.ACCEPTED).build()
  }

  @Tag(name = "Jda requests")
  @Operation(
    summary = "Asynchronous request to jda worker.",
    description = "This api endpoint is for sending asynchronous request  to jda worker.  Requires role ROLE_JUSTICE_DATA_AGENT_REQUESTS",
    security = [SecurityRequirement(name = "JUSTICE_DATA_AGENT_REQUESTS")],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response from LLM"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden to access this endpoint. The issue can be logged staff and prisoner have different establishment.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @GetMapping("dequeueresponse")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_REQUESTS')")
  suspend fun dequeueResponse(): ResponseEntity<JdaResponse> {
    val jdaResponse = jdaWorkerService.dequeueResponse()
    return ResponseEntity.status(HttpStatus.OK).body(jdaResponse)
  }
}
