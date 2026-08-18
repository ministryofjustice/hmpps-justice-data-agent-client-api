package uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.utility.DataGenerator

class HmppsJdaWorkerApiExtension :
  BeforeAllCallback,
  AfterAllCallback,
  BeforeEachCallback {
  companion object {
    @JvmField
    val jdaWorkerService = HmppsJdaWorkerMockServer()
  }

  override fun beforeAll(context: ExtensionContext) {
    jdaWorkerService.start()
    jdaWorkerService.stubSubmitRequest()
  }

  override fun beforeEach(context: ExtensionContext) {
    jdaWorkerService.resetRequests()
  }

  override fun afterAll(context: ExtensionContext) {
    jdaWorkerService.stop()
  }
}

class HmppsJdaWorkerMockServer : WireMockServer(WIREMOCK_PORT) {
  companion object {
    private const val WIREMOCK_PORT = 8091
  }

  fun stubSubmitRequest() {
    stubFor(
      post(urlEqualTo("/v1/submitrequest"))
        .willReturn(
          aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              DataGenerator.buildJdaResponse()
            ),
        ),
    )
  }

  fun stubQueueRequest() {
    stubFor(
      post(urlEqualTo("/v1/queuerequest"))
        .willReturn(
          aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              DataGenerator.buildJdaResponse()
            ),
        ),
    )
  }
}
