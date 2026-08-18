package uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration

import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration.LocalStackContainer.setLocalStackProperties
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration.wiremock.HmppsAuthApiExtension
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration.wiremock.HmppsAuthApiExtension.Companion.hmppsAuth
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration.wiremock.HmppsJdaWorkerApiExtension
import uk.gov.justice.hmpps.sqs.HmppsQueue
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.test.kotlin.auth.JwtAuthorisationHelper

@ExtendWith(HmppsAuthApiExtension::class, HmppsJdaWorkerApiExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
abstract class IntegrationTestBase {

  @Autowired
  protected lateinit var hmppsQueueService: HmppsQueueService

  internal val jdaResponseQueue by lazy { hmppsQueueService.findByQueueId("jdaresponsequeus") as HmppsQueue }
  internal val awsSqsClient by lazy { jdaResponseQueue.sqsClient }
  internal val queueUrl by lazy { jdaResponseQueue.queueUrl }

  companion object {
    @JvmStatic
    private val localStackContainer = LocalStackContainer.instance

    @JvmStatic
    @DynamicPropertySource
    fun localstackProperties(registry: DynamicPropertyRegistry) {
      localStackContainer?.also { setLocalStackProperties(it, registry) }
    }

    @BeforeAll
    @JvmStatic
    fun startContainer() {
      localStackContainer?.start()
    }
  }

  internal fun getNumberOfMessagesCurrentlyOnQueue(awsSqsClient: SqsAsyncClient, queueUrl: String): Int? {
    val queueAttributes = awsSqsClient.getQueueAttributes(
      GetQueueAttributesRequest.builder().queueUrl(queueUrl).attributeNamesWithStrings("ApproximateNumberOfMessages").build(),
    ).get()
    return queueAttributes.attributesAsStrings()["ApproximateNumberOfMessages"]?.toInt()
  }

  @BeforeEach
  fun `Wait for empty queue`() {
    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(awsSqsClient, queueUrl) } matches { it == 0 }
  }

  @Autowired
  protected lateinit var webTestClient: WebTestClient

  @Autowired
  protected lateinit var jwtAuthHelper: JwtAuthorisationHelper

  internal fun setAuthorisation(
    username: String? = "AUTH_ADM",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf("read"),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisationHeader(username = username, scope = scopes, roles = roles)

  protected fun stubPingWithResponse(status: Int) {
    hmppsAuth.stubHealthPing(status)
  }
}
