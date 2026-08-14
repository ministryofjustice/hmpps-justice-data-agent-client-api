package uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.RequestType
import java.time.LocalDateTime

@JsonInclude(Include.NON_NULL)
data class MetaData(
  val type: RequestType,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  val submittedAt: LocalDateTime?,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  val queuedAt: LocalDateTime?,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  val processedAt: LocalDateTime?,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  val receivedAt: LocalDateTime?,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  val completedAt: LocalDateTime?,
)
