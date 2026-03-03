package com.example.workspace.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Spring Boot Workspace API")
            .description("REST API for the Spring Boot Workspace application")
            .version("0.0.1-SNAPSHOT")
            .contact(new Contact()
                .name("Spring Boot Workspace")));
  }
}

