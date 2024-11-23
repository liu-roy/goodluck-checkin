package com.goodluck.checkin;


//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
//@EnableFeignClients
//@EnableDiscoveryClient
//@MapperScan({"com.goodluck.checkin.domain.*.mapper"})
@SpringBootApplication
@ComponentScan("com.goodluck")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
