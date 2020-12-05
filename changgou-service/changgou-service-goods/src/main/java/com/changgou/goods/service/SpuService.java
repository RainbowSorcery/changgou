package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;

public interface SpuService {
    void addGoods(Goods goods);

    Goods findGoodsBySpuId(String spuId);

    void update(Goods goods);

    void aduitGoods(String spuId);

    void upGoods(String spuId);

    void downGoods(String spuId);

    void deleteLogic(String spuId);

    void resotre(String spuId);

    void deleteGoods(String spuId);
}
