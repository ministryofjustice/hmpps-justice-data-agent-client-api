package uk.gov.justice.digital.hmpps.justicedataagentclientapi.config

import org.springframework.boot.tomcat.TomcatConnectorCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer

@Configuration
class SecurityConfig {
  @Bean // skipping auth check on this point for developer testing, this wil be removed once credential added in env and required role
  fun webSecurityCustomizer(): WebSecurityCustomizer? = WebSecurityCustomizer { web: WebSecurity -> web.ignoring().requestMatchers("/v1/**") }

  @Bean
  fun asyncTimeoutCustomize(): TomcatConnectorCustomizer = TomcatConnectorCustomizer { connector -> connector.asyncTimeout = 180000 }
}
