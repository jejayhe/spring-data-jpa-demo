package com.example.demo.config;

import com.alibaba.fastjson.JSON;
import com.example.demo.dto.Customer;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.MyOrderRepository;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
@Component
@RequiredArgsConstructor
public class LocalCache {

    @Autowired
    private MyOrderRepository myOrderRepository;
    @Autowired
    private  CustomerRepository customerRepository;
    private static ApplicationContext applicationContext;

    public LoadingCache<Long, String> CustomerLocalCache;
//    public static Function<Long, String> getCustomer(Long id){
////        return t -> new Date().toString();
//        return t -> {
//        return JSON.toJSONString(myCustomer);};
//    }
    public String getCustomerDao(Long id){
        return JSON.toJSONString(customerRepository.findById(id));
    }

    public static LocalCache instance = null;

    public synchronized static LocalCache getInstance(){
        if (instance==null){
            instance = applicationContext.getBean(LocalCache.class);
        }
        return instance;
    }
    @PostConstruct
    private void init(){
        CustomerLocalCache = Caffeine.newBuilder()
                .maximumSize(3)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(k -> getCustomerDao(k));
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        LocalCache.applicationContext = applicationContext;
    }

}
