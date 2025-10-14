package com.example.common_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // ======================================================
    // 🧩 RabbitMQ 접속정보 (application.yml에서 주입)
    // ======================================================
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    // ======================================================
    // 🧱 기본 연결 설정
    // ======================================================
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory factory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public AmqpAdmin amqpAdmin(CachingConnectionFactory factory) {
        RabbitAdmin admin = new RabbitAdmin(factory);
        admin.setAutoStartup(true); // 컨텍스트 초기화 시 큐 자동 등록
        return admin;
    }

    // ======================================================
    // 📦 ALERT 모듈 (테스트용 Exchange / Queue / RoutingKey)
    // ======================================================
    public static final String ALERT_EXCHANGE = "alert.exchange";
    public static final String ALERT_QUEUE = "alert.queue";
    public static final String ALERT_ROUTING_KEY = "alert.key";

    @Bean
    public DirectExchange alertExchange() {
        return new DirectExchange(ALERT_EXCHANGE);
    }

    @Bean
    public Queue alertQueue() {
        return new Queue(ALERT_QUEUE, true);
    }

    @Bean
    public Binding alertBinding(Queue alertQueue, DirectExchange alertExchange) {
        return BindingBuilder.bind(alertQueue)
                .to(alertExchange)
                .with(ALERT_ROUTING_KEY);
    }
}
