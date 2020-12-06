package com.changgou.controller;

import com.changgou.service.ElasticSearchSearchService;
import com.changgou.service.impl.ElasticSearchSearchServiceImpl;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private ElasticSearchSearchService elasticSearchSearchService;

    @Autowired
    private ElasticSearchSearchServiceImpl elasticSe;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @GetMapping("/list")
    public Map<String, Object> findList(@RequestParam Map<String, String> searchConditionMap) {


        return elasticSearchSearchService.search(searchConditionMap);
    }

    @GetMapping("/testList")
    public Map<String, Object> testList(@RequestParam Map<String, Object> searchConditionMap) {

        return elasticSe.searchTest(searchConditionMap);
    }
}
