package uk.gov.justice.digital.hmpps.justicedataagentclientapi.resource

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.JdaWorkerService
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse

@RestController
@RequestMapping(value = ["v1"])
class JdaWorkerResource(private val jdaWorkerService: JdaWorkerService) {

  @PostMapping("submitrequest")
  @PreAuthorize("hasAnyRole('JUSTICE_DATA_AGENT_REQUESTS')")
  suspend fun submitSynchronousRequest(@RequestBody jdaRequest: JdaRequest): ResponseEntity<JdaResponse> {
    val response = jdaWorkerService.submitSynchronousRequest(jdaRequest)
    return ResponseEntity(response, HttpStatus.OK)
  }
}
