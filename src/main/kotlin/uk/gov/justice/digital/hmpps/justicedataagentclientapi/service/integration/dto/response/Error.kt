package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response

data class Error(
  val code: String,
  val message: String,
  val stage: Stage,
)
