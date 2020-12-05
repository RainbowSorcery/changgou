package com.changgou.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.changgou.goods.dao")
public class ChanggouServiceGoodsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChanggouServiceGoodsApplication.class, args);
	}

}
