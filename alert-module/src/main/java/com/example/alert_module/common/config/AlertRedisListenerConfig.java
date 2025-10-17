//package com.example.alert_module.common.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//
//@Configuration
//@RequiredArgsConstructor
//public class AlertRedisListenerConfig {
//
//    private final StockUpdateListener stockUpdateListener;
//
//    @Bean
//    public RedisMessageListenerContainer redisListenerContainer(RedisConnectionFactory connectionFactory) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.addMessageListener(stockUpdateListener, new ChannelTopic("stock:update"));
//        return container;
//    }
//}