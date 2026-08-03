package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.client.JdaWorkerClient
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse

@Service
class JdaWorkerServiceImpl(private val jdaWorkerClient: JdaWorkerClient) : JdaWorkerService {
  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)
  }
  override suspend fun submitSynchronousRequest(jdaRequest: JdaRequest): JdaResponse = jdaWorkerClient.submitSynchronousRequest(jdaRequest)

  override suspend fun submitNonBlockingRequest(jdaRequest: JdaRequest): JdaResponse {
    TODO("Not yet implemented")
  }
}
