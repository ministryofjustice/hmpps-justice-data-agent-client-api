package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class RequestType {
  @JsonProperty("sync")
  SYNC,

  @JsonProperty("async")
  ASYNC,
}
