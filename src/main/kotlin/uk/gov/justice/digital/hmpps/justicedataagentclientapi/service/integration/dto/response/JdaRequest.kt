package uk.gov.justice.digital.hmpps.justicedataagentworker.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.UUID

/*
{
  "requestId": "<uuid>"
  "correlationId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "prompt": {
    "key": "referral-annotation",
    "version": 3
  },
  "status":  "queued | processing | succeeded | failed | rejected"
  "metaData": {
    "requestType": "sync" | "async",
    "submittedAt": "2026-06-27T09:55:03Z",
    "queuedAt": "2026-06-27T09:55:03Z",
    "receivedAt": "2026-06-27T09:55:03Z",
    "completedAt": "2026-06-27T09:55:03Z",
    "queuedAt": "2026-06-27T09:55:03Z",
   },
}
 */
data class JdaRequest(
  // val requestId: UUID,
  val correlationId: UUID,
  val prompt: Prompt,
  val requestData: Any,
)

data class Prompt(
  val key: String,
  val version: Int,
  // val metaData: MetaData,
)

data class MetaData(
  val type: RequestType,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  val submittedAt: LocalDateTime,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  val queuedAt: LocalDateTime,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  val processedAt: LocalDateTime,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  val receivedAt: LocalDateTime,
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  val completedAt: LocalDateTime,
)

enum class RequestType(value: String) {
  SYNC("sync"),
  ASYNC("async"),
}

data class Error(
  val code: String,
  val message: String,
  val stage: Stage,
)

enum class Stage(value: String) {
  INTERNAL("internal"),
  GATEWAY("gateway"),
  SKILL("skill"),
  VALIDATION("validation"),
}
