package com.example.user_module.common.config;

import com.example.common_service.config.SwaggerConfigInterface;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig implements SwaggerConfigInterface {

    @Bean
    public GroupedOpenApi userGroupedOpenApi() {
        return createGroupedOpenApi("auth", "/auth/**", "Auth API", "사용자 업무 처리를 위한 API");
    }
}