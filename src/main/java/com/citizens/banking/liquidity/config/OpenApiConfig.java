package com.citizens.banking.liquidity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI liquidityServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Liquidity Service API")
                .description("Citizens Banking Liquidity Microservice APIs")
                .version("v1"));
    }
}
