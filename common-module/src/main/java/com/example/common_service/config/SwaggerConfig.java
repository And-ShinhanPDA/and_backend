package com.example.common_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi alertApi() {
        return GroupedOpenApi.builder()
                .group("Alert")
                .packagesToScan("com.example.alert_module")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("User")
                .packagesToScan("com.example.user_module")
                .build();
    }

    @Bean
    public GroupedOpenApi dataApi() {
        return GroupedOpenApi.builder()
                .group("Data")
                .packagesToScan("com.example.data_process_module")
                .build();
    }

    @Bean
    public OpenAPI baseOpenAPI() {
        final String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("AND Backend API Docs")
                        .description("Alert, User, Data 모듈 통합 API 명세서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요. (형식: Bearer <token>)")
                ));
    }
}