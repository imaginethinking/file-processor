package dev.arunsamra.common.web.security;

import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@AutoConfiguration(after = JwtPublicKeyAutoConfiguration.class)
@ConditionalOnBean(RSAPublicKey.class)
@EnableConfigurationProperties(JwtValidationProperties.class)
public class JwtValidationAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(JwtDecoder.class)
  JwtDecoder jwtDecoder(RSAPublicKey publicKey, JwtValidationProperties properties) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();

    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.issuer()));

    return decoder;
  }
}
