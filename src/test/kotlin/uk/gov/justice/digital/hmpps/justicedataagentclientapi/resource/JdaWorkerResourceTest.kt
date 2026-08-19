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
  @param:Value("\${hmpps.sqs.queues.jdarequestqueues.queuename}") val jdaRequestQueueName: String,
  @param:Value("\${hmpps.sqs.queues.jdaresponsequeues.queuename}") val jdaResponseQueueName: String,
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
            .findByQueueId("jdaresponsequeues")!!.sqsClient,
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
    // Get message from jda request queue.
    var messages = requestQueueAwsSqsClient.receiveMessage(
      ReceiveMessageRequest.builder()
        .maxNumberOfMessages(1)
        .queueUrl(requestQueueUrl)
        .build(),
    )?.join()
    // Verify jd request queue is empty.
    assertEquals(0, messages?.messages()?.size)

    // send jda request to endpoint /v1/queuerequest

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

    // Get message from queue to verify it get added in jda request queue by call to endpoint /v1/queuerequest.
    messages = requestQueueAwsSqsClient.receiveMessage(
      ReceiveMessageRequest.builder()
        .maxNumberOfMessages(1)
        .queueUrl(requestQueueUrl)
        .build(),
    )?.join()
    assertEquals(1, messages?.messages()?.size)

    // Get jda request message body from jda request queue.
    val jdaRequest = objectMapper.readValue(messages?.messages()[0]?.body(), JdaRequest::class.java)

    // Assert message added in jda request queue after api call to endpoint v1/queuerequest
    assertEquals(correlationId, jdaRequest.correlationId)
    assertEquals(promptKey, jdaRequest.prompt.key)
    assertEquals(version, jdaRequest.prompt.version)

    // Send request to dequeue message for jda response queue.
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

    // Verify no message in jda response queue after call to endpoint /v1/dequeueresponse.
    messages = requestQueueAwsSqsClient.receiveMessage(
      ReceiveMessageRequest.builder()
        .maxNumberOfMessages(1)
        .queueUrl(responseQueueUrl)
        .build(),
    )?.join()
    assertEquals(0, messages?.messages()?.size)
  }
}
