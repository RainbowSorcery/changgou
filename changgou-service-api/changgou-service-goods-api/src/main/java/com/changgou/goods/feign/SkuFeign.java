package com.changgou.goods.feign;

import com.changgou.goods.entity.Result;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// name为微服务名称
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    @GetMapping("/findBySpuId/{spuId}")
    public List<Sku> findSpuIdBySku(@PathVariable String spuId);

    @GetMapping("/findAll")
    public Result<List<Sku>> findAllSku();
}
