package com.example.demo;

import com.example.demo.producer.Producer;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMQ {
    @Autowired
    private Producer producer;

    @org.junit.Test
    public void testSendTopicMsg() throws InterruptedException{
        Thread.sleep(1000);
//        for(int i=0; i<10; i++){
//            producer.sendTopicText("第："+ i +"跳testSendTopicMsg测试消息");
//        }
    }
}
