package com.example.demo.config;


import com.example.demo.dto.Customer;
import com.example.demo.producer.Producer;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.vo.CustomerVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class LocalCache implements ApplicationContextAware {

    @Autowired
    private MyOrderRepository myOrderRepository;
    @Autowired
    private  CustomerRepository customerRepository;
    private static ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Producer producer;

    private static ExecutorService executorService = new ThreadPoolExecutor(0, 50, 60L, TimeUnit.SECONDS, new SynchronousQueue());

    public class RedisOpExecutable implements Runnable {
        private String key;
        private String val;
        private String op;
        public RedisOpExecutable(String op, String key, String val){
            this.op=op;
            this.key=key;
            this.val=val;
        }
        public RedisOpExecutable(String op, String key){
            this.op=op;
            this.key=key;
        }
        @Override
        public void run(){
            if (this.op.equals("set")) {
                redisTemplate.opsForValue().set(key, val, 60, TimeUnit.SECONDS);
            } else if (op.equals("del")){
                redisTemplate.delete(key);
            }
        }
    }

    public AsyncLoadingCache<Long, String> CustomerLocalCache;
//    public static Function<Long, String> getCustomer(Long id){
////        return t -> new Date().toString();
//        return t -> {
//        return JSON.toJSONString(myCustomer);};
//    }
    public String getCustomerDao(Long id) throws Exception{
//        ObjectMapper mapper = new ObjectMapper();
//        // if redis success, return
//        Object val = redisTemplate.opsForValue().get("customer:"+id.toString());
//        System.out.println("read redis:"+ Objects.toString(val));
//        if (val!=null){
//            CustomerVo c = mapper.readValue(((String)val).getBytes(), CustomerVo.class);
//            return mapper.writeValueAsString(c);
//        }
//        // if redis fails
//        Customer customer = customerRepository.findById(id);
//        // set redis
//
//        //Converting the Object to JSONString
////        String jsonString = mapper.writeValueAsString(std);
//        executorService.execute(new RedisOpExecutable("set", "customer:"+id.toString(), mapper.writeValueAsString(customer)));
////        redisTemplate.opsForValue().set("customer:"+id.toString(), mapper.writeValueAsString(customer), 60, TimeUnit.SECONDS);
////        redisTemplate.opsForValue().set("customer:"+id.toString(), "{\"id\":1,\"firstName\":\"Jack\",\"lastName\":\"Bauer\"}", 60, TimeUnit.SECONDS);
//        return mapper.writeValueAsString(customer);
        if (id.equals(1L)) {
            Thread.sleep(8000);
            System.out.println("query takes 8 seconds");
        } else{
            System.out.println("took no time");
        }
        return "haha";
    }
    public void setCustomerDao(Long id) throws InterruptedException{
        ObjectMapper mapper = new ObjectMapper();
        Thread.sleep(20);
        // delete cache from redis and local
//        redisTemplate.delete("customer:"+id.toString());
        executorService.execute(new RedisOpExecutable("del", "customer:"+id.toString()));
        producer.sendTopicCustomer(id.toString());
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
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .buildAsync(k -> getCustomerDao(k));
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        LocalCache.applicationContext = applicationContext;
    }

}
