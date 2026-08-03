package uk.gov.justice.digital.hmpps.justicedataagentclientapi.resource

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.JdaWorkerService
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse

@RestController
@RequestMapping(value = ["v1"])
class JdaWorkerResource(private val jodaWorkerService: JdaWorkerService) {

  @PostMapping("submitrequest")
  suspend fun submitSynchronousRequest(@RequestBody jdaRequest: JdaRequest): ResponseEntity<JdaResponse> {
    val response = jodaWorkerService.submitSynchronousRequest(jdaRequest)
    return ResponseEntity(response, HttpStatus.OK)
  }
}