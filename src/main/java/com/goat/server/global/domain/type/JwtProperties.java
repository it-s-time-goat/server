package com.goat.server.global.domain.type;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private String issuer;
    private String secretKey;
    private Long tokenExpirationTime;
    private Long refreshTokenExpirationTime;
}