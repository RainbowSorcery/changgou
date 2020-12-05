package com.changgou.service.impl;

import com.changgou.dao.SearchMapper;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.listener.SkuUpListener;
import com.changgou.pojo.SkuInfo;
import com.changgou.service.ElasticMannagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticSearchMannageServiceImpl implements ElasticMannagerService {
    // 搜索和管理用template
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    // mapper 只用于存数据以及数据的更新
    @Autowired
    private SearchMapper searchMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private IndexOperations indexOperations;

    @Override
    public void deleteIndexAndMapping() {
        indexOperations.delete();
    }

    @Override
    public void createIndexAndMapping() {
        indexOperations.create();
        Document skuInfoDocument = indexOperations.createMapping(SkuInfo.class);
        indexOperations.putMapping(skuInfoDocument);
    }

    @Override
    public void importAll() {
        List<Sku> skuListData = skuFeign.findAllSku().getData();

        SkuUpListener.skuJsonToSkuInfoList(skuListData, searchMapper);
    }




}