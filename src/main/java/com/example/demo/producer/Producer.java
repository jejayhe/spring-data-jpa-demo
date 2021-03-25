package com.example.demo.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import javax.jms.Topic;

@Component
public class Producer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;


    @Autowired
    private Topic testTopic;

    @Autowired
    private Topic customerTopic;



    public void sendTopicText(String msg)  {
        this.jmsMessagingTemplate.convertAndSend(this.testTopic, msg);
    }
    public void sendTopicCustomer(String msg)  {
        this.jmsMessagingTemplate.convertAndSend(this.customerTopic, msg);
    }

}