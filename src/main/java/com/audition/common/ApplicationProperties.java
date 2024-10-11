package com.audition.common;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
@NonFinal
@Value
@Slf4j
public class ApplicationProperties {

    @Valid Endpoint endpoint;

    @PostConstruct
    void printProperties() {
        log.info("Printing Application Properties");
        log.info("Application Configuration Properties\n{}", this);
    }

    @Value
    @NonFinal
    @Validated
    public static class Endpoint {
        @NotBlank
        String postUrl;
        @NotBlank
        String commentsUrl;
    }
}
