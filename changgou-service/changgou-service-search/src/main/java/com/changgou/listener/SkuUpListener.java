package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.dao.SearchMapper;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.pojo.SkuInfo;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RabbitListener(queues = {"search_add_queue"})
@Component
public class SkuUpListener {
    @Autowired
    private SearchMapper searchMapper;

    @Autowired
    private SkuFeign skuFeign;

    @RabbitHandler
    public void searchAddQueueHandle(String spuId) {
        // 获取sku列表
        List<Sku> skuList = skuFeign.findSpuIdBySku(spuId);

        skuJsonToSkuInfoList(skuList, searchMapper);
    }

    public static void skuJsonToSkuInfoList(List<Sku> skuList, SearchMapper searchMapper) {
        String skuListJson = JSON.toJSONString(skuList);

        List<SkuInfo> skuInfos = JSON.parseArray(skuListJson, SkuInfo.class);

        if (skuInfos != null && skuInfos.size() > 0) {
            skuInfos.forEach((skuInfo -> {
                Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
                skuInfo.setSpecMap(map);
            }));
        }

        assert skuInfos != null;
        searchMapper.saveAll(skuInfos);
    }
}
