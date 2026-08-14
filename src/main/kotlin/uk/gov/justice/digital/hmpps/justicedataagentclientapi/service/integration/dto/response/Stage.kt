package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response

enum class Stage(value: String) {
  INTERNAL("internal"),
  GATEWAY("gateway"),
  SKILL("skill"),
  VALIDATION("validation"),
}
