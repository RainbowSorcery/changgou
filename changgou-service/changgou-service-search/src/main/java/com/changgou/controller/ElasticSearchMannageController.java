package com.changgou.controller;

import com.changgou.dao.SearchMapper;
import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.pojo.SkuInfo;
import com.changgou.service.ElasticMannagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mannage")
public class ElasticSearchMannageController {
    @Autowired
    private ElasticMannagerService elasticMannagerService;

    @PostMapping("/createIndexAndMapping")
    public Result<String> createIndexMapping() {
        elasticMannagerService.createIndexAndMapping();

        return new Result<>(true, StatusCode.OK, "索引创建成功.");
    }

    @PostMapping("/deleteIndexAndMapping")
    public Result<String> deleteIndexAndMapping() {
        elasticMannagerService.deleteIndexAndMapping();

        return new Result<>(true, StatusCode.OK, "删除索引成功.");
    }

    @PostMapping("/importAll")
    public Result<String> importAll() {
        elasticMannagerService.importAll();

        return new Result<>(true, StatusCode.OK, "数据导入成功。");
    }
}
