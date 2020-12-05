package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
// 扫描feign接口
@EnableFeignClients(basePackages = "com.changgou.goods.feign")
public class ChanggouServiceSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChanggouServiceSearchApplication.class, args);
	}

}
