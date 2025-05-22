package com.notificationmicroservice.config;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQBrokerConfig {

    @Bean
    public BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false); // In-memory only
        broker.setUseJmx(true);
        broker.addConnector("tcp://localhost:61616"); // Expose to external applications on this URL
        return broker;
    }
}