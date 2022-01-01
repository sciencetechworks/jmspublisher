package com.stw.jmspublisher;

import com.stw.shared.News;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableJms
@EnableScheduling
@Slf4j
public class JmspublisherApplication {

    private static ConfigurableApplicationContext context;

    @Scheduled(fixedRate = 2500)
    public void sendMessage() throws JMSException {
        if (context==null)
            return;
        
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        Topic springTopic = jmsTemplate.getConnectionFactory()
                .createConnection()
                .createSession().createTopic("spring");
        News news = new News(100, "Latest news on Spring");
        jmsTemplate.convertAndSend(springTopic, news);
        log.info("News sent");
    }

    public static void main(String[] args) throws JmsException, JMSException {
        context = SpringApplication.run(JmspublisherApplication.class, args);
    }
}
