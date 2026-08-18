package uk.gov.justice.digital.hmpps.justicedataagentclientapi.integration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.io.IOException
import java.net.ServerSocket

object LocalStackContainer {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)
  val instance by lazy { startLocalstackIfNotRunning() }

  fun setLocalStackProperties(localStackContainer: LocalStackContainer, registry: DynamicPropertyRegistry) {
    registry.add("hmpps.sqs.enabled") { "true" }
    registry.add("hmpps.sqs.localstackUrl") { localStackContainer.endpoint }
    registry.add("hmpps.sqs.region") { localStackContainer.region }
    registry.add("spring.cloud.aws.credentials.access-key") { localStackContainer.getAccessKey() }
    registry.add("spring.cloud.aws.credentials.secret-key") { localStackContainer.getSecretKey() }
    registry.add("spring.cloud.aws.region.static") { localStackContainer.getRegion() }
    registry.add("spring.cloud.aws.sqs.endpoint") {
      localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS).toString()
    }
  }

  private fun startLocalstackIfNotRunning(): LocalStackContainer? {
    if (localstackIsRunning()) {
      log.warn("Using existing localstack instance")
      return null
    }
    log.info("Creating a localstack instance")
    val logConsumer = Slf4jLogConsumer(log).withPrefix("localstack")
    return LocalStackContainer(
      DockerImageName.parse("localstack/localstack").withTag("4"),
    ).apply {
      withServices(LocalStackContainer.Service.SQS)
      withEnv("HOSTNAME_EXTERNAL", "localhost")
      withEnv("DEFAULT_REGION", "eu-west-2")
      waitingFor(
        Wait.forLogMessage(".*Ready.*", 1),
      )
      start()
      followOutput(logConsumer)
    }
  }

  private fun localstackIsRunning(): Boolean = try {
    val serverSocket = ServerSocket(4566)
    serverSocket.localPort == 0
  } catch (_: IOException) {
    true
  }
}
