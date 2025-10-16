package com.example.common_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    
//    @Bean
//    public CachingConnectionFactory connectionFactory() {
//        CachingConnectionFactory factory = new CachingConnectionFactory();
//        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
//        factory.setPublisherReturns(true);
//        return factory;
//    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ LocalDateTime 지원
        mapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            CachingConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        // 성능/안정성 튜닝
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(5);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false); // 예외 시 재큐잉 방지

        return factory;
    }

    // ======================================================
    // 📦 ALERT 모듈 (테스트용 Exchange / Queue / RoutingKey)
    // ======================================================
    public static final String ALERT_EXCHANGE = "alert.exchange";
    public static final String ALERT_QUEUE = "alert.queue";
    public static final String ALERT_ROUTING_KEY = "alert.key";

    public static final String ALERT_TEST_QUEUE = "alert.test.queue";
    public static final String ALERT_TEST_ROUTING_KEY = "alert.test.key";

    // 2) 테스트용 큐/바인딩 추가 (같은 exchange 재사용)
    @Bean
    public Queue alertTestQueue() {
        return new Queue(ALERT_TEST_QUEUE, true);
    }

    @Bean
    public Binding alertTestBinding(Queue alertTestQueue, DirectExchange alertExchange) {
        return BindingBuilder.bind(alertTestQueue)
                .to(alertExchange)
                .with(ALERT_TEST_ROUTING_KEY);
    }

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
