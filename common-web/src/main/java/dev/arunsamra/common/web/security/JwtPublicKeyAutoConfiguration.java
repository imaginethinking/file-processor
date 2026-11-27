package dev.arunsamra.common.web.security;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "security.jwt.validation", name = "public-key-location")
@EnableConfigurationProperties(JwtValidationProperties.class)
public class JwtPublicKeyAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(RSAPublicKey.class)
  RSAPublicKey jwtPublicKey(JwtValidationProperties properties) throws Exception {
    try (InputStream inputStream = properties.publicKeyLocation().getInputStream()) {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

      Certificate certificate = certificateFactory.generateCertificate(inputStream);

      return (RSAPublicKey) certificate.getPublicKey();
    }
  }
}
