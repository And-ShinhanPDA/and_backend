package com.example.search_module.config;

import com.example.common_service.config.SwaggerConfigInterface;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration    // 스프링 실행시 설정파일 읽어드리기 위한 어노테이션
public class SwaggerConfig implements SwaggerConfigInterface {

    @Bean
    public GroupedOpenApi tradeGroupedOpenApi() {
        return createGroupedOpenApi("trade", "/trade/**", "Trade API", "자동매매 업무 처리를 위한 API");
    }
}