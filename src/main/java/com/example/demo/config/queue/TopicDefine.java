package com.example.demo.config.queue;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicDefine {
    @Bean(name = "testTopic")
    public javax.jms.Topic testTopic() {
        return new ActiveMQTopic("test.topic");
    }
}
