package uk.gov.justice.digital.hmpps.justicedataagentclientapi.health

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.hmpps.kotlin.health.HealthPingCheck

@Component
class JdaWorkerHealthPingCheck(@Qualifier("hmppsJdaWorkerHealthWebClient") webClient: WebClient) : HealthPingCheck(webClient)