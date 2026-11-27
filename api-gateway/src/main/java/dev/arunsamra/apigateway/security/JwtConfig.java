package dev.arunsamra.apigateway.security;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

  @Bean
  JwtKeyPair jwtKeyPair(JwtProperties properties) throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");

    try (InputStream inputStream = properties.keystoreLocation().getInputStream()) {
      keyStore.load(inputStream, properties.keystorePassword().toCharArray());
    }

    Key key = keyStore.getKey(properties.keyAlias(), properties.keystorePassword().toCharArray());

    Certificate certificate = keyStore.getCertificate(properties.keyAlias());

    return new JwtKeyPair((RSAPublicKey) certificate.getPublicKey(), (RSAPrivateKey) key);
  }

  @Bean
  RSAPublicKey jwtPublicKey(JwtKeyPair keyPair) {
    return keyPair.publicKey();
  }

  @Bean
  JwtEncoder jwtEncoder(JwtKeyPair keyPair) {
    return NimbusJwtEncoder.withKeyPair(keyPair.publicKey(), keyPair.privateKey()).build();
  }
}
