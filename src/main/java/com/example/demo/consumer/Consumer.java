package com.example.demo.consumer;

import com.example.demo.config.LocalCache;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @JmsListener(destination = "test.topic", containerFactory = "jmsListenerContainerTopic")
    private void getTopicMessageText(final String text) {
        /*try {
            // 模拟业务处理
            System.out.println("getTopicMessageText 休息10s");
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            System.out.println("继续运行");
        } catch (InterruptedException e) {
            throw new RuntimeException("业务处理异常");
        }*/
        System.out.println("*****************************************************************");
        System.out.println("线程ID = " + Thread.currentThread().getId());
        System.out.println("消费者接受topic信息：" + text);
    }

    @JmsListener(destination = "customer.topic", containerFactory = "jmsListenerContainerTopic")
    private void getCustomerMessageText(final String text) {
        /*try {
            // 模拟业务处理
            System.out.println("getTopicMessageText 休息10s");
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            System.out.println("继续运行");
        } catch (InterruptedException e) {
            throw new RuntimeException("业务处理异常");
        }*/
        System.out.println("*****************************************************************");
        System.out.println("线程ID = " + Thread.currentThread().getId());
        System.out.println("消费者接受Customer信息：" + text);
        Long id = Long.valueOf(text);
        LocalCache.getInstance().CustomerLocalCache.synchronous().invalidate(id);
    }

}
