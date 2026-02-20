package com.example.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI cafeAppOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Cafe App API")
                        .description("API documentation for Cafe POS & Mobile Customer Ordering System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Cafe App Team")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }

    @Bean
    public GroupedOpenApi staffApi() {
        return GroupedOpenApi.builder()
                .group("1-staff-api")
                .displayName("Staff / Admin API")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/mobile/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mobileApi() {
        return GroupedOpenApi.builder()
                .group("2-mobile-api")
                .displayName("Mobile Customer API")
                .pathsToMatch("/api/mobile/**")
                .build();
    }
}
