package com.changgou.goods.service;

import com.changgou.goods.pojo.Sku;

import java.util.List;

public interface SkuService {
    List<Sku> findBySpuId(String spuId);

    List<Sku> findAll();

}
