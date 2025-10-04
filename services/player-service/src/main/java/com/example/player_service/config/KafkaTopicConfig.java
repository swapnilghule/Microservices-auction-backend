//package com.example.player_service.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Bean
//    public NewTopic playerEventsTopic() {
//        return new NewTopic("playerEvents", 3, (short) 1); // 3 partitions, 1 replication
//    }
//
//    @Bean
//    public NewTopic playerPoolEventsTopic() {
//        return new NewTopic("playerPoolEvents", 3, (short) 1);
//    }
//
//    @Bean
//    public NewTopic playerStatusEventsTopic() {
//        return new NewTopic("playerStatusEvents", 3, (short) 1);
//    }
//}