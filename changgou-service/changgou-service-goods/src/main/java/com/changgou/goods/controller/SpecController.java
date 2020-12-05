package com.changgou.goods.controller;

import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spec")
public class SpecController {
    @Autowired
    private SpecService specService;

    @GetMapping("/cate/spec/{categoryName}")
    public Result<Spec> findByCategoryNameBySpec(@PathVariable String categoryName) {
        return new Result<Spec>(true, StatusCode.OK,"根据分类查询规格成功.", specService.findByCategoryNameBySpec(categoryName));
    }
}
