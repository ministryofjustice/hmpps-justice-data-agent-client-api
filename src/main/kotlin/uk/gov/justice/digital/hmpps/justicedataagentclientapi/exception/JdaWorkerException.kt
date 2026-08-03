package uk.gov.justice.digital.hmpps.justicedataagentclientapi.exception

import org.springframework.http.HttpStatus
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

class JdaWorkerException(override val message: String, val status: HttpStatus, val response: ErrorResponse ) : RuntimeException(message)