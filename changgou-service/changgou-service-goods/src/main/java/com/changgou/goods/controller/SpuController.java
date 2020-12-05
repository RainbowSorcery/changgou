package com.changgou.goods.controller;

import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spu")
public class SpuController {
    @Autowired
    private SpuService spuService;

    @PostMapping("/addGoods")
    public Result<Goods> addBrand(@RequestBody Goods goods ) {

        spuService.addGoods(goods);
        return new Result<>(true, StatusCode.OK, "添加商品成功.");
    }

    @GetMapping("/findGoodBySpuid/{spuId}")
    public Result<Goods> findGoodsBySpuId(@PathVariable String spuId) {
        Goods goods = spuService.findGoodsBySpuId(spuId);

        return new Result<>(true, StatusCode.OK, "查询商品成功", goods);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody Goods goods) {
        spuService.update(goods);

        return new Result<>(true, StatusCode.OK, "修改商品信息成功.");
    }

    @PostMapping("/auditGoods/{spuId}")
    public Result<String> auditGoods(@PathVariable String spuId) {
        spuService.aduitGoods(spuId);

        return new Result<>(true, StatusCode.OK, "审核修改成功");
    }

    @PostMapping("/upGo  ods/{spuId}")
    public Result<String> upGoods(@PathVariable String spuId) {
        spuService.upGoods(spuId);

        return new Result<>(true, StatusCode.OK, "上架成功.");
    }

    @PostMapping("/downGoods/{spuId}")
    public Result<String> downGoods(@PathVariable String spuId) {
        spuService.downGoods(spuId);

        return new Result<>(true, StatusCode.OK, "下架成功.");
    }

    @PostMapping("/deleteLogic/{spuId}")
    public Result<String> deleteLogic(@PathVariable String spuId) {
        spuService.deleteLogic(spuId);

        return new Result<>(true, StatusCode.OK, "逻辑删除商品成功.");
    }

    @PostMapping("/restore/{spuId}")
    public Result<String> resotre(@PathVariable String spuId) {
        spuService.resotre(spuId);

        return new Result<>(true, StatusCode.OK, "商品已恢复.");
    }

    @PostMapping("/deleteGoods/{spuId}")
    public Result<String> deleteGoods(@PathVariable String spuId) {
        spuService.deleteGoods(spuId);

        return new Result<>(true, StatusCode.OK, "商品物理删除成功.");
    }
}
