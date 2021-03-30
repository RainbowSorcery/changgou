package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.changgou.feign"})
public class ChanggouWebOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChanggouWebOrderApplication.class, args);
    }

}
