package uk.gov.justice.digital.hmpps.justicedataagentclientapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.hmpps.kotlin.auth.authorisedWebClient
import uk.gov.justice.hmpps.kotlin.auth.healthWebClient
import java.time.Duration

@Configuration
class WebClientConfiguration(
  @param:Value("\${hmpps.jda.worker.api.base-url}") private val jdaWorkerBaseUrl: String,
  @param:Value("\${hmpps-auth.url}") val hmppsAuthBaseUri: String,
  @param:Value("\${api.health-timeout:2s}") val healthTimeout: Duration,
  @param:Value("\${api.timeout:20s}") val timeout: Duration,
  @Value("\${api.timeout:10s}") val apiTimeout: Duration,
) {
  // HMPPS Auth health ping is required if your service calls HMPPS Auth to get a token to call other services
  @Bean
  fun hmppsAuthHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(hmppsAuthBaseUri, healthTimeout)

  @Bean
  fun hmppsJdaWorkerHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(jdaWorkerBaseUrl, healthTimeout)

  @Bean
  fun hmppsJdaWorkerWebClient(authorizedClientManager: OAuth2AuthorizedClientManager, builder: WebClient.Builder): WebClient = builder.authorisedWebClient(
    authorizedClientManager,
    registrationId = "jda-worker-api",
    url = jdaWorkerBaseUrl,
    Duration.ofSeconds(18000L),
  )

  // @Bean
  // fun hmppsJdaWorkerWebClient(builder: WebClient.Builder): WebClient = builder.baseUrl(jdaWorkerBaseUrl).build()
}
