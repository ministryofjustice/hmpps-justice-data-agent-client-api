package uk.gov.justice.digital.hmpps.justicedataagentclientapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JusticeDataAgentClientApi

fun main(args: Array<String>) {
  runApplication<JusticeDataAgentClientApi>(*args)
}
