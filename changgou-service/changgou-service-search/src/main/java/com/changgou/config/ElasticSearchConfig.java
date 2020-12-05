package com.changgou.config;

import com.changgou.pojo.SkuInfo;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;

@Configuration
public class ElasticSearchConfig {

    @Bean
    public IndexOperations indexOperations(@Autowired ElasticsearchOperations elasticsearchOperations) {
        return elasticsearchOperations.indexOps(SkuInfo.class);
    }
}

