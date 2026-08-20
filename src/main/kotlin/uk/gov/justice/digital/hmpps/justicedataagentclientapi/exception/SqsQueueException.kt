package uk.gov.justice.digital.hmpps.justicedataagentclientapi.exception

class SqsQueueException(override val message: String) : RuntimeException(message)
