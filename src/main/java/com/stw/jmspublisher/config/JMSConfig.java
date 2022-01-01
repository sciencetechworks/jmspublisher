package com.stw.jmspublisher.config;

import javax.jms.JMSException;
import lombok.extern.slf4j.Slf4j;
//import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
@Slf4j
public class JMSConfig {

    private String brokerUrl;

    private String user;

    private String password;

    @Autowired
    public JMSConfig(@Value("${spring.activemq.broker-url}") String brokerUrl,
            @Value("${spring.activemq.user}") String user,
            @Value("${spring.activemq.password}") String password) {
        this.brokerUrl = brokerUrl;
        this.user = user;
        this.password = password;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsContainerFactory() {
        DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
        containerFactory.setPubSubDomain(true);
        containerFactory.setConnectionFactory(connectionFactory());
        containerFactory.setMessageConverter(jacksonJmsMessageConverter());
        return containerFactory;
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory cachConnectionFactory = new CachingConnectionFactory();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        try {
            connectionFactory.setBrokerURL(brokerUrl);
        } catch (JMSException ex) {
            log.error(ex.toString());
        }
        connectionFactory.setPassword(password);
        connectionFactory.setUser(user);
        cachConnectionFactory.setTargetConnectionFactory(connectionFactory);
        return cachConnectionFactory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
