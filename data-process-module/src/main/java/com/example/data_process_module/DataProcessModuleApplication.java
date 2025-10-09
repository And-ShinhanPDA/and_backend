package com.example.data_process_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.example")
public class DataProcessModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataProcessModuleApplication.class, args);
	}

}
