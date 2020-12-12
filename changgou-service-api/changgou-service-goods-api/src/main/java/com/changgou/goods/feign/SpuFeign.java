package com.changgou.goods.feign;

import com.changgou.goods.entity.Result;
import com.changgou.goods.pojo.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "goods")
@RequestMapping("/spu")
public interface SpuFeign {
    @GetMapping("/findGoodBySpuid/{spuId}")
    public Result<Goods> findGoodsBySpuId(@PathVariable String spuId);
}
