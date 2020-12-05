package com.changgou.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ChanggouEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChanggouEurekaApplication.class, args);
	}

}
