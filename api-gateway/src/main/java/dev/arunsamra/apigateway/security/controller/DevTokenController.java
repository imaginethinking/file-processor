package dev.arunsamra.apigateway.security.controller;

import dev.arunsamra.apigateway.security.JwtIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dev")
public class DevTokenController {

    private final JwtIssuer jwtIssuer;

    @GetMapping("/token")
    public String token() {
        return jwtIssuer.issue();
    }

}
