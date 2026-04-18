package com.challenge.bandsapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bandsApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bands API")
                        .description("Backend challenge REST API that exposes a music bands catalog with caching support.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("bands-api")
                                .url("https://bands-api.vercel.app")));
    }
}

