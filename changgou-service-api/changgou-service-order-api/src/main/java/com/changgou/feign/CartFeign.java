package com.changgou.feign;

/**
 * @author rainbow
 * @date 2021/1/17
 */

import com.changgou.goods.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "ORDER-SERVICE")
@RequestMapping("/cart")
public interface CartFeign {
    @PostMapping("/add")
    public Result<String> addCart(@RequestParam("username") String username,
                                  @RequestParam("skuId") String skuId,
                                  @RequestParam("num") Integer num);

    @GetMapping("/list")
    public Map<String, Object> catdList(@RequestParam("username") String username);

    @DeleteMapping("/delete")
    public Result<String> deleteCart(@RequestParam("username") String username,
                                     @RequestParam("skuId") String skuId);

    @PutMapping("/updateChecked")
    public Result<String> updateChecked(@RequestParam("username") String username,
                                        @RequestParam("checked") boolean checked,
                                        @RequestParam("skuId") String skuId);
}
