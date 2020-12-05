package com.changgou.service.impl;
import com.alibaba.fastjson.JSON;
import com.changgou.goods.pojo.Spec;
import com.google.common.collect.Maps;

import com.changgou.dao.SearchMapper;
import com.changgou.pojo.SkuInfo;
import com.changgou.service.ElasticSearchSearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ElasticSearchSearchServiceImpl implements ElasticSearchSearchService {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Map<String, Object> search(Map<String, String> conditionMap) {
        Map<String,Object> stringMap=Maps.newHashMap();

        Map<String, Object> resultMap = new HashMap<>();

        // 判断map中是否又字段
        if (!StringUtils.isEmpty(conditionMap.get("keywords"))) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // 组合查询调教
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", conditionMap.get("keywords")).operator(Operator.AND));

            NativeSearchQueryBuilder nativeSearchQueryBuilder =
                    new NativeSearchQueryBuilder();
            
            // 需求2.1 根据品牌进行分组 设置分组查询
            // AggregationBuilders.terms("").field(); term()为group名称 field为分组字段名称
            // 相当于
            // {
            //    "aggs": {
            //      "groupName": {
            //        "terms": {
            //          "field": "brandName"
            //        }
            //      }
            //    }
            //  }
            String brandGroup = "brandGroup";
            TermsAggregationBuilder brandGroupBuilder = AggregationBuilders.terms(brandGroup).field("brandName");
            // 添加至聚合搜索对象
            nativeSearchQueryBuilder.addAggregation(brandGroupBuilder);

            //



            String categoryBrand = "categoryGroup";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryBrand).field("categoryName"));

            String specGroup = "specGroup";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(specGroup).field("spec.keyword"));

            int currentPage = 0;

            int pageSize = 5;
            // 判断字段中是否有currentPage和pageSize如果有则使用map中的值 如果没有或不为数字则使用默认值 也就是0 和 5
            if (StringUtils.isNumeric(conditionMap.get("currentPage"))) {
                currentPage = Integer.parseInt(conditionMap.get("currentPage"));
            }

            if (StringUtils.isNumeric(conditionMap.get("pageSize"))) {
                pageSize = Integer.parseInt(conditionMap.get("pageSize"));
            }

            // withPageable 为分页查询
            NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder
                    .withQuery(boolQueryBuilder)
                    .withPageable(PageRequest.of(currentPage, pageSize)).build();

            SearchHits<SkuInfo> searchData = elasticsearchRestTemplate.search(nativeSearchQuery, SkuInfo.class);

            // 获取聚合搜索结果
            Aggregations aggregations = searchData.getAggregations();
            assert aggregations != null;
            Aggregation brandAggregation = aggregations.get(brandGroup);

            // 得转成term才可以获取到内容
            Terms brandTerms = (Terms) brandAggregation;

            List<String> brandList = new LinkedList<>();
            // 遍历保存至brandList中
            brandTerms.getBuckets().forEach((bucket -> {
                brandList.add(bucket.getKeyAsString());
            }));

            Terms categoryTerms = aggregations.get(categoryBrand);
            List<String> categoryList = new LinkedList<>();

            categoryTerms.getBuckets().forEach((bucket -> {
                categoryList.add(bucket.getKeyAsString());
            }));

            Terms specTerms = aggregations.get(specGroup);

            Map<String, Set<String>> specMap = new HashMap<>();

            specTerms.getBuckets().forEach((bucket -> {
                // 将规格转为map
                Map<String, String>specJsonMap = JSON.parseObject(bucket.getKeyAsString(), Map.class);

                Set<String>specSet = null;

                for (String key : specJsonMap.keySet()) {
                    if (specMap.containsKey(key)) {
                        specSet = specMap.get(key);
                    } else {
                        specSet = new HashSet<>();
                    }
                    // 因为使用set去重了 所以不用担心每次添加进去重复的值
                    specSet.add(specJsonMap.get(key));
                    specMap.put(key, specSet);
                }
            }));


            // 将SearchHits转为SearchPage
            SearchPage<SkuInfo>skuInfoSearchPage = SearchHitSupport.searchPageFor(searchData, nativeSearchQuery.getPageable());

            resultMap.put("rows", skuInfoSearchPage.getContent());
            resultMap.put("total", skuInfoSearchPage.getTotalElements());
            resultMap.put("totalPage", skuInfoSearchPage.getTotalPages());
            resultMap.put("brandList", brandList);
            resultMap.put("categoryList", categoryList);
            resultMap.put("specMap", specMap);
        }

        return resultMap;
    }
    
    
}