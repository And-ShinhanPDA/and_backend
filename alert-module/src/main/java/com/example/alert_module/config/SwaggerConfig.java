package com.example.alert_module.config;

import com.example.common_service.config.SwaggerConfigInterface;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig implements SwaggerConfigInterface {


    @Bean
    public GroupedOpenApi alertGroupedOpenApi() {
        return createGroupedOpenApi("Alert", "/alert/**", "Alert API", "알림 업무 처리를 위한 API");
    }
//    @Bean
//    public OpenAPI openAPI() {
//        SecurityScheme securityScheme = new SecurityScheme()
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer")
//                .bearerFormat("JWT")
//                .in(SecurityScheme.In.HEADER)
//                .name("Authorization");
//
//        SecurityRequirement securityRequirement = new SecurityRequirement()
//                .addList("bearerAuth");
//
//        return new OpenAPI()
//                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
//                .security(Arrays.asList(securityRequirement))
//                .info(apiInfo());
//    }
//
//    private Info apiInfo() {
//        return new Info()
//                .title("Alert API")
//                .description("알림에 관한 REST API")
//                .version("1.0.0");
//    }
}
