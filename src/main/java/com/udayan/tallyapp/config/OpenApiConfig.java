package com.udayan.tallyapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Biswajit Debnath",
                        email = "biswajit.sust@gmail.com"
                ),
                description = "OpenApi Documentation for Spring Security",
                title = "OpenApi specification - Biswajit Debnath",
                version = "1.0",
                termsOfService = "Terms of service"
        ),
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

        @Value("${server.port}")
        private String port;

        @Value("${server.servlet.context-path}")
        private String contextPath;

        @Bean
        public io.swagger.v3.oas.models.OpenAPI customOpenAPI() {
                String baseUrl = "http://localhost:" + port + contextPath;
                return new io.swagger.v3.oas.models.OpenAPI()
                        .addServersItem(new io.swagger.v3.oas.models.servers.Server().url(baseUrl).description("Server URL"));
        }
}
