package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.changgou.user.feign")
public class ChanggouServiceOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChanggouServiceOrderApplication.class, args);
    }

}
