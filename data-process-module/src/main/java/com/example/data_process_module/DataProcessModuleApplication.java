package com.example.data_process_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class DataProcessModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataProcessModuleApplication.class, args);
	}

}
