package dev.arunsamra.apigateway.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public record JwtKeyPair(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey
) {
}
