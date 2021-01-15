package com.changgou.goods.controller;

import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sku")
public class SkuController {
    @Autowired
    private SkuService skuService;

    @GetMapping("/findAll")
    public Result<List<Sku>> findAllSku() {
        List<Sku> skuList = skuService.findAll();

        return new Result<>(true, StatusCode.OK, "查询所有sku数据成功.", skuList);
    }

    @GetMapping("/findBySpuId/{spuId}")
    public List<Sku> findBySpuId(@PathVariable String spuId) {
        return skuService.findBySpuId(spuId);
    }

    @GetMapping("/findSkuById/{skuId}")
    public Result<Sku> findSkuById(@PathVariable String skuId) {
        Sku sku = skuService.findSkuById(skuId);

        return new Result<>(true, StatusCode.OK, "根据skuId查询sku成功", sku);
    }
}
