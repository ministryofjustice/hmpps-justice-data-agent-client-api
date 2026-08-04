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
    val response = hmppsJdaWorkerWebClient.post()
      .uri("/v1/sendrequest")
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
    hmppsJdaWorkerWebClient.get()
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
  }

  suspend fun getPromptByKeyAndVersion(key: String, version: Int?): PromptResponse {
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
