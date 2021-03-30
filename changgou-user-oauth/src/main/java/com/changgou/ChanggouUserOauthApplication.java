package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.changgou.auth.dao")
@EnableFeignClients(basePackages = "com.changgou.user.feign")
@EnableDiscoveryClient
public class ChanggouUserOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChanggouUserOauthApplication.class, args);
    }

}
