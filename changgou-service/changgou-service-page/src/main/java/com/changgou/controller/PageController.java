package com.changgou.controller;

import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController

@RequestMapping ("/page")
public class PageController {
    @Autowired
    private PageService pageService;

    @GetMapping("/createPageHttml/{spuId}")
    public Result<String> createPage(@PathVariable String spuId) {
        pageService.createPageHtml(spuId);

        return new Result<>(true, StatusCode.OK, "生成静态页面成功");
    }


}
