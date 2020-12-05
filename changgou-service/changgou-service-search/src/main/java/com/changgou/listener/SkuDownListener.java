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

import java.util.List;
import java.util.Map;

@Component
@RabbitListener(queues = {"goods_down_queue"})
public class SkuDownListener {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SearchMapper searchMapper;

    @RabbitHandler
    public void downSkuHandle(String spuId) {
        List<Sku> skuList = skuFeign.findSpuIdBySku(spuId);

        String skuListJsonString = JSON.toJSONString(skuList);

        List<SkuInfo> skuInfos = JSON.parseArray(skuListJsonString, SkuInfo.class);

        if (skuInfos != null && skuInfos.size() > 0) {
            skuInfos.forEach((skuInfo -> {
                Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
                skuInfo.setSpecMap(map);
            }));
        }

        searchMapper.deleteAll(skuInfos);
    }
}
