package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class RequestType(private val value: String) {
  SYNC("sync"),
  ASYNC("async"),
  ;

  fun getValue(): String = value

  override fun toString(): String = value
}
