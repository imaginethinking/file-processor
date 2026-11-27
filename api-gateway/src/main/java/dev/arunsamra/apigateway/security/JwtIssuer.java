package dev.arunsamra.apigateway.security;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtIssuer {

  private static final String ISSUER = "api-gateway";
  private static final String SUBJECT = "gateway";

  private final JwtEncoder jwtEncoder;
  private final JwtProperties jwtProperties;

  public String issue() {
    Instant now = Instant.now();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(ISSUER)
            .subject(SUBJECT)
            .issuedAt(now)
            .expiresAt(now.plus(jwtProperties.tokenTtl()))
            .build();

    JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

    return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }
}
