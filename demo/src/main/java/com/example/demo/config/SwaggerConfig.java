package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("People API")
                        .version("1.0")
                        .description("API для управления людьми"));
    }

    @Bean
    public GroupedOpenApi measurementsApi() {
        return GroupedOpenApi.builder()
                .group("measurements")
                .pathsToMatch("/measurements/**")
                .build();
    }

    @Bean
    public GroupedOpenApi sensorsApi() {
        return GroupedOpenApi.builder()
                .group("sensors")
                .pathsToMatch("/sensors/**")
                .build();
    }
}