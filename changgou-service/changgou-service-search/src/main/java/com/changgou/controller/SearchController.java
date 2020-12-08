package com.changgou.controller;

import com.changgou.goods.entity.Page;
import com.changgou.pojo.SkuInfo;
import com.changgou.service.ElasticSearchSearchService;
import com.changgou.service.impl.ElasticSearchSearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private ElasticSearchSearchService elasticSearchSearchService;

    @Autowired
    private ElasticSearchSearchServiceImpl elasticSe;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @GetMapping("/list")
    public String findList(@RequestParam Map<String, String> searchConditionMap, Model model) {
        Map<String, Object> searchMap = elasticSearchSearchService.search(searchConditionMap);

        Page<Long> skuInfoPage = new Page<>((Long) searchMap.get("total"), (int)searchMap.get("pageNum"), (int)searchMap.get("pageSize"));

        StringBuilder url = new StringBuilder("/search/list?");

        int flag = 0;

        for (String key : searchConditionMap.keySet()) {
            // 因为前端已经拼接过了 当前端有key的话 跳过拼接
            if (!"pageNum".equals(key) && !"sortField".equals(key) && !"sortRule".equals(key)) {
                url.append(key).append("=").append(searchConditionMap.get(key)).append("&");
            }
        }

        // 删除最后一个字符&
        url.deleteCharAt(url.length() - 1);


        model.addAttribute("result", searchMap);
        model.addAttribute("searchMap", searchConditionMap);
        model.addAttribute("page", skuInfoPage);
        model.addAttribute("url", url);

        return "search";
    }

    @GetMapping("/testList")
    public Map<String, Object> testList(@RequestParam Map<String, String> searchConditionMap) {
        Map<String, Object> searchMap = elasticSearchSearchService.search(searchConditionMap);

        return searchMap;
    }
}
