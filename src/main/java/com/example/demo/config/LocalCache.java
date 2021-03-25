package com.example.demo.config;


import com.example.demo.dto.Customer;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.vo.CustomerVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
@Component
@RequiredArgsConstructor
public class LocalCache {

    @Autowired
    private MyOrderRepository myOrderRepository;
    @Autowired
    private  CustomerRepository customerRepository;
    private static ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate redisTemplate;

    public LoadingCache<Long, String> CustomerLocalCache;
//    public static Function<Long, String> getCustomer(Long id){
////        return t -> new Date().toString();
//        return t -> {
//        return JSON.toJSONString(myCustomer);};
//    }
    public String getCustomerDao(Long id) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        // if redis success, return
        Object val = redisTemplate.opsForValue().get("customer:"+id.toString());
        System.out.println("read redis:"+ Objects.toString(val));
        if (val!=null){
            CustomerVo c = mapper.readValue(((String)val).getBytes(), CustomerVo.class);
            return mapper.writeValueAsString(c);
        }
        // if redis fails
        Customer customer = customerRepository.findById(id);
        // set redis

        //Converting the Object to JSONString
//        String jsonString = mapper.writeValueAsString(std);
        redisTemplate.opsForValue().set("customer:"+id.toString(), mapper.writeValueAsString(customer), 60, TimeUnit.SECONDS);
//        redisTemplate.opsForValue().set("customer:"+id.toString(), "{\"id\":1,\"firstName\":\"Jack\",\"lastName\":\"Bauer\"}", 60, TimeUnit.SECONDS);
        return mapper.writeValueAsString(customer);
//        return "haha";
    }
//    public String setCustomerDao(Long id){
//        // if redis fails
//        Customer customer = customerRepository.findById(id);
//        // sleep 20 ms
//        // delete
//        return JSON.toJSONString(customerRepository.findById(id));
//    }

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
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build(k -> getCustomerDao(k));
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        LocalCache.applicationContext = applicationContext;
    }

}
