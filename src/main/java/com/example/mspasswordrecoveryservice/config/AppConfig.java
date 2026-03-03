package com.example.mspasswordrecoveryservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "token")
@Getter
@Setter
public class AppConfig {
    private int expiration;
}
