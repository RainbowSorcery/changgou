package com.changgou.goods.config;

import com.changgou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdWorkConfig {
    @Bean
    public IdWorker idWorker() {
        return new IdWorker(0, 0);
    }
}
