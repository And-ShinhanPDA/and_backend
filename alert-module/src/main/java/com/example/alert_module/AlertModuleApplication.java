package com.example.alert_module;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRabbit
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class AlertModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertModuleApplication.class, args);
	}

}
