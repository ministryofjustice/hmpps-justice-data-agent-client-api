plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.5.0"
  kotlin("plugin.spring") version "2.4.0"
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.5.0")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-webclient")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:7.3.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.2")
  // Localstack
  implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
  testImplementation("org.testcontainers:localstack:1.21.4")
  testImplementation("org.awaitility:awaitility-kotlin:4.3.0")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.5.0")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
  testImplementation("org.wiremock:wiremock-standalone:3.13.2")
  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.44") {
    exclude(group = "io.swagger.core.v3")
  }
}

kotlin {
  jvmToolchain(25)
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
  }
}
