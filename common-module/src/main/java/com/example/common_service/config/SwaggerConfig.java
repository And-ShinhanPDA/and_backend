package com.example.common_service.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Alert 모듈용 Swagger 그룹
     */
    @Bean
    public GroupedOpenApi alertApi() {
        return GroupedOpenApi.builder()
                .group("Alert")
                .packagesToScan("com.example.alert_module")
                .build();
    }

    /**
     * User 모듈용 Swagger 그룹
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("User")
                .packagesToScan("com.example.user_module")
                .build();
    }

    /**
     * Search 모듈용 Swagger 그룹 (있을 경우)
     */
    @Bean
    public GroupedOpenApi searchApi() {
        return GroupedOpenApi.builder()
                .group("Search")
                .packagesToScan("com.example.search_module")
                .build();
    }

    /**
     * 공통 API 정보
     */
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AND Backend API Docs")
                        .description("Alert, User, Search 등 모듈 통합 API 명세서")
                        .version("v1.0.0"));
    }
}