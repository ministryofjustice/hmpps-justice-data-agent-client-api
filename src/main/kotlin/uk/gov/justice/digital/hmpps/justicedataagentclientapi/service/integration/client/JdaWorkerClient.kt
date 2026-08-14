package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.client

import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.exception.JdaWorkerException
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.PromptRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response.PromptsResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.JdaResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response.PromptResponse
import uk.gov.justice.digital.hmpps.justicedataagentworker.exception.NotFoundException
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Component
class JdaWorkerClient(private val hmppsJdaWorkerWebClient: WebClient) {

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  suspend fun submitSynchronousRequest(jdaRequest: JdaRequest): JdaResponse {
    logger.info("Sending synchronous request to jda worker for correlation id: ${jdaRequest.correlationId}")
    val response = hmppsJdaWorkerWebClient.post()
      .uri("/v1/submitrequest")
      .bodyValue(jdaRequest)
      .retrieve()
      .bodyToMono(JdaResponse::class.java)
      .onErrorResume { e ->
        e as WebClientResponseException
        val error = e.getResponseBodyAs(ErrorResponse::class.java)
        logger.error("Error connecting to jdaworking: ${e.message}")
        Mono.error { throw JdaWorkerException(e.message, HttpStatus.valueOf(e.statusCode.value()), error!!) }
      }
      .blockOptional().orElseThrow { NotFoundException("Error connecting to jdaworking") }
    return response
  }

  suspend fun createPrompt(promptRequest: PromptRequest): PromptResponse {
    logger.info("Sending request to Jda Worker for adding new prompt for client: ${promptRequest.createdBy}")
    val response = hmppsJdaWorkerWebClient.post()
      .uri("/v1/prompts")
      .bodyValue(promptRequest)
      .retrieve()
      .bodyToMono(PromptResponse::class.java)
      .onErrorResume { e ->
        e as WebClientResponseException
        val error = e.getResponseBodyAs(ErrorResponse::class.java)
        logger.error("Error connecting to jdaworking: ${e.message}")
        Mono.error { throw JdaWorkerException(e.message, HttpStatus.valueOf(e.statusCode.value()), error!!) }
      }
      .blockOptional().orElseThrow()
    return response
  }

  suspend fun updatePrompt(key: String, promptRequest: PromptRequest): PromptResponse {
    logger.info("Sending request to Jda Worker for updating prompt for key: ${promptRequest.promptKey}")
    val response = hmppsJdaWorkerWebClient.put()
      .uri("/v1/prompts/$key")
      .bodyValue(promptRequest)
      .retrieve()
      .bodyToMono(PromptResponse::class.java)
      .onErrorResume { e ->
        e as WebClientResponseException
        val error = e.getResponseBodyAs(ErrorResponse::class.java)
        logger.error("Error connecting to jdaworking: ${e.message}")
        Mono.error { throw JdaWorkerException(e.message, HttpStatus.valueOf(e.statusCode.value()), error!!) }
      }
      .blockOptional().orElseThrow()
    return response
  }

  suspend fun getPrompts(): List<PromptsResponse> {
    logger.info("Sending request to Jda worker to retrieve  all prompt.")
    val response = hmppsJdaWorkerWebClient.get()
      .uri("/v1/prompts")
      .retrieve()
      .bodyToMono(object : ParameterizedTypeReference<List<PromptsResponse>>() {})
      .onErrorResume { e ->
        e as WebClientResponseException
        val error = e.getResponseBodyAs(ErrorResponse::class.java)
        logger.error("Error connecting to jdaworking: ${e.message}")
        Mono.error { throw JdaWorkerException(e.message, HttpStatus.valueOf(e.statusCode.value()), error!!) }
      }
      .blockOptional().orElseThrow()
    return response
  }

  suspend fun getPromptByKey(key: String): PromptResponse {
    logger.info("Sending request to Jda worker to retrieve  prompt by key: $key")
    val response = hmppsJdaWorkerWebClient.get()
      .uri("/v1/prompts/$key")
      .retrieve()
      .bodyToMono(PromptResponse::class.java)
      .onErrorResume { e ->
        e as WebClientResponseException
        val error = e.getResponseBodyAs(ErrorResponse::class.java)
        logger.error("Error connecting to jdaworking: ${e.message}")
        Mono.error { throw JdaWorkerException(e.message, HttpStatus.valueOf(e.statusCode.value()), error!!) }
      }
      .blockOptional().orElseThrow()
    return response
  }

  suspend fun deletePromptByKey(key: String) {
    logger.info("Sending request to Jda worker to delete prompt by key: $key.")
    hmppsJdaWorkerWebClient.delete()
      .uri("/v1/prompts/$key")
      .retrieve()
      .bodyToMono(Void::class.java)
      .onErrorResume { e ->
        e as WebClientResponseException
        val error = e.getResponseBodyAs(ErrorResponse::class.java)
        logger.error("Error connecting to jdaworking: ${e.message}")
        Mono.error { throw JdaWorkerException(e.message, HttpStatus.valueOf(e.statusCode.value()), error!!) }
      }
      .blockOptional().orElseThrow()
  }

  suspend fun getPromptByKeyAndVersion(key: String, version: Int?): PromptResponse {
    logger.info("Sending request to Jda worker to retrieve  prompt by key: $key and version: $version")
    val response = hmppsJdaWorkerWebClient.get()
      .uri("/v1/prompts/$key/versions/$version")
      .retrieve()
      .bodyToMono(PromptResponse::class.java)
      .onErrorResume { e ->
        e as WebClientResponseException
        val error = e.getResponseBodyAs(ErrorResponse::class.java)
        logger.error("Error connecting to jdaworking: ${e?.message}")
        Mono.error { throw JdaWorkerException(e.message!!, HttpStatus.valueOf(e.statusCode.value()), error!!) }
      }
      .blockOptional().orElseThrow()
    return response
  }
}
