package com.example.search_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = "com.example")
public class SearchModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchModuleApplication.class, args);
	}

}
