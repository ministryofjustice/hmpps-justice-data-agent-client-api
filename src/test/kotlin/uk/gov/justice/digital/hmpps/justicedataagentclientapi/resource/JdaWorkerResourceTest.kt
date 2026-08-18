package uk.gov.justice.digital.hmpps.justicedataagentclientapi.resource

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.event.JdaMessagePublisherImpl.Companion.logger
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.utility.DataGenerator
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse
import java.time.Duration
import java.util.*

class JdaWorkerResourceTest(
  @Autowired private val objectMapper: ObjectMapper,
  @param:Value("\${hmpps.sqs.queues.jdarequestqueus.queuename}") val jdaRequestQueueName: String,
  @param:Value("\${hmpps.sqs.queues.jdaresponsequeus.queuename}") val jdaResponseQueueName: String,
) : IntegrationTestBase() {
  @BeforeEach
  internal fun setUp() {
    webTestClient = webTestClient
      .mutate()
      .responseTimeout(Duration.ofMillis(30000))
      .build()

    val sqsTemplate =
      SqsTemplate
        .newTemplate(
          hmppsQueueService
            .findByQueueId("jdaresponsequeus")!!.sqsClient,
        )
    logger.info("Sending jda request message to queue")
    sqsTemplate.send { to -> to.queue("sqs_response_queue_name").payload(ObjectMapper().readTree(DataGenerator.buildJdaResponse())) }
  }

  @AfterEach
  internal fun tearDown() {
  }

  @Test
  fun `submit synchronous request`() {
    webTestClient.post().uri("/v1/submitrequest")
      .headers(setAuthorisation(roles = listOf("ROLE_JUSTICE_DATA_AGENT_REQUESTS")))
      .header("Content-Type", "application/json")
      .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .bodyValue(
        DataGenerator.buildJdaRequest(UUID.randomUUID(), "PROMPT_KEY_1", 1),
      )
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
      .expectBody(object : ParameterizedTypeReference<JdaResponse>() {})
      .consumeWith(System.out::println)
      .returnResult()
      .responseBody as JdaResponse
  }

  @Test
  fun `submit queue request and  get dequeue response`() {
    val correlationId = UUID.randomUUID()
    val promptKey = "prompt key 1"
    val version = 1
    webTestClient.post().uri("/v1/queuerequest")
      .headers(setAuthorisation(roles = listOf("ROLE_JUSTICE_DATA_AGENT_REQUESTS")))
      .header("Content-Type", "application/json")
      .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .bodyValue(
        DataGenerator.buildJdaRequest(correlationId, promptKey, version),
      )
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isAccepted

    // Get message from queue to verify it get added in queue by call to endpoint /v1/queuerequest
    var sqsClient = hmppsQueueService
      .findByQueueId("jdarequestqueus")!!.sqsClient
    var queueUrl = sqsClient.getQueueUrl(
      GetQueueUrlRequest.builder()
        .queueName(jdaRequestQueueName)
        .build(),
    )?.join()?.queueUrl()
    var messages = sqsClient.receiveMessage(
      ReceiveMessageRequest.builder()
        .maxNumberOfMessages(1)
        .queueUrl(queueUrl)
        .build(),
    )?.join()

    val jdaRequest = objectMapper.readValue(messages?.messages()[0]?.body(), JdaRequest::class.java)

    // assert message in queue
    assertEquals(correlationId, jdaRequest.correlationId)
    assertEquals(promptKey, jdaRequest.prompt.key)
    assertEquals(version, jdaRequest.prompt.version)

    assertEquals(1, messages?.messages()?.size)

    webTestClient.get().uri("/v1/dequeueresponse")
      .headers(setAuthorisation(roles = listOf("ROLE_JUSTICE_DATA_AGENT_REQUESTS")))
      .header("Content-Type", "application/json")
      .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
      .expectBody(object : ParameterizedTypeReference<JdaResponse>() {})
      .consumeWith(System.out::println)
      .returnResult()
      .responseBody as JdaResponse

    // assert no message in queue
    // assert message in jda response queue
    sqsClient = hmppsQueueService
      .findByQueueId("jdaresponsequeus")!!.sqsClient
    queueUrl = sqsClient.getQueueUrl(
      GetQueueUrlRequest.builder()
        .queueName(jdaResponseQueueName)
        .build(),
    )?.join()?.queueUrl()
    // Get message from queue to verify it has been dequeued
    messages = sqsClient.receiveMessage(
      ReceiveMessageRequest.builder()
        .maxNumberOfMessages(1)
        .queueUrl(queueUrl)
        .build(),
    )?.join()
    assertEquals(0, messages?.messages()?.size)
  }
}
