package com.changgou.service.impl;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import com.changgou.pojo.SkuInfo;
import com.changgou.service.ElasticSearchSearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ElasticSearchSearchServiceImpl implements ElasticSearchSearchService {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private final NativeSearchQueryBuilder nativeSearchQueryBuilder =
            new NativeSearchQueryBuilder();

    private final BoolQueryBuilder boolQueryBuilder =
            new BoolQueryBuilder();

    private boolean flag = true;


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

            // 精确查找 根据品牌名称精确查询
            if (!StringUtils.isEmpty(conditionMap.get("brandName"))) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("brandName", conditionMap.get("brandName")));
            }

            if (!StringUtils.isEmpty(conditionMap.get("categoryName"))) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("categoryName", conditionMap.get("categoryName")));
            }

            List<String> specNameList = new ArrayList<>();

            // 获取所有以specMap开头的key
            conditionMap.keySet().forEach((key) -> {
                // 判断开头是否为specMap.的key
                if (key.startsWith("specMap.")) {
                    specNameList.add(key);
                }
            });

            // 拼接查询
            specNameList.forEach((specName) -> {
                if (!StringUtils.isEmpty(conditionMap.get(specName))) {
                    boolQueryBuilder.must(QueryBuilders.matchQuery(specName, conditionMap.get(specName)));
                }
            });

            if (!StringUtils.isEmpty(conditionMap.get("priceRange"))) {
                String priceRange = conditionMap.get("priceRange");

                String[] split = priceRange.split("-");

                String price1 = split[0];
                String price2 = split[1];

                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(price1).lte(price2));


            }

            // 排序查询
            if (!StringUtils.isEmpty(conditionMap.get("sortField")) && !StringUtils.isEmpty(conditionMap.get("sortRule"))) {
                String sortField = conditionMap.get("sortField");
                String sortRule = conditionMap.get("sortRule");

                if ("DESC".equals(sortRule)) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC));
                }
            }

            // 高亮查询
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<font color='red'>");
            highlightBuilder.postTags("</font>");
            highlightBuilder.field("name");
            nativeSearchQueryBuilder.withHighlightBuilder(highlightBuilder);


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


            // 高亮字段
            for (SearchHit<SkuInfo> skuInfoSearchHit : skuInfoSearchPage) {
                // 获取高亮列表
                Map<String, List<String>> highlightFields = skuInfoSearchHit.getHighlightFields();

                SkuInfo content = skuInfoSearchHit.getContent();
                // 将高亮内容覆盖
                content.setName(highlightFields.get("name").get(0));
            }


            resultMap.put("rows", skuInfoSearchPage.getContent());
            resultMap.put("total", skuInfoSearchPage.getTotalElements());
            resultMap.put("totalPage", skuInfoSearchPage.getTotalPages());
            resultMap.put("brandList", brandList);
            resultMap.put("categoryList", categoryList);
            resultMap.put("specMap", specMap);
        }

        return resultMap;
    }


    /**
     * 分页查询
     * @param searchData 搜索数据
     * @return 分页对象
     */
    public SearchPage<SkuInfo> findPage(Map<String, Object> searchConditionMap, SearchHits<SkuInfo> searchData) {

        int currentPage = 0;
        if (searchConditionMap.get("currentPage") != null) {
            currentPage = Integer.parseInt((String) searchConditionMap.get("currentPage"));
        }

        int pageSize = 10;
        if (searchConditionMap.get("pageSize") != null) {
            pageSize = Integer.parseInt((String) searchConditionMap.get("pageSize"));
        }


        nativeSearchQueryBuilder.withPageable(PageRequest.of(currentPage, pageSize));

        return SearchHitSupport.searchPageFor(searchData, nativeSearchQueryBuilder.build().getPageable());
    }


    private void keyWordSearch(Map<String, Object> searchConditionMap) {
        String keywords = null;
        if (!StringUtils.isEmpty((String) searchConditionMap.get("keywords"))) {
            keywords = (String) searchConditionMap.get("keywords");
        }

        assert keywords != null;
        boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords));

        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
    }

    private void specSearch() {

    }

    private List<String> polymerizationSearch(SearchHits<SkuInfo> searchHits, String groupName) {

        Aggregations aggregations = searchHits.getAggregations();


        assert aggregations != null;
        Terms brandTerms = aggregations.get(groupName);

        List<String> brandList = new ArrayList<>();

        brandTerms.getBuckets().forEach((bucket -> {
            brandList.add(bucket.getKeyAsString());
        }));

        return brandList;
    }

    private void setAggregation(String groupName, String fieldName) {

        TermsAggregationBuilder brandAggregation = AggregationBuilders.terms(groupName).field(fieldName);
        nativeSearchQueryBuilder.addAggregation(brandAggregation);
    }



    public Map<String, Object> searchTest(Map<String, Object> searchConditionMap) {
        Map<String, Object> resultMap = new HashMap<>();

        keyWordSearch(searchConditionMap);

        String brandGroupName = "brandGroup";
        String categoryGroupName = "categoryGroup";

        // 因为每次调用都会向 nativeSearchQueryBuilder 中添加Aggregation 所有设置一个标识只调用一次方法
        if (flag) {
            setAggregation(brandGroupName, "brandName");
            setAggregation(categoryGroupName, "categoryName");


            flag = false;
        }

        SearchHits<SkuInfo> searchHits =
                elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), SkuInfo.class);

        List<String> brandList = polymerizationSearch(searchHits, brandGroupName);
        List<String> categoryList = polymerizationSearch(searchHits, categoryGroupName);


        SearchPage<SkuInfo> searchPageData = findPage(searchConditionMap, searchHits);


        resultMap.put("rows", searchPageData.getContent());
        resultMap.put("total", searchPageData.getTotalElements());
        resultMap.put("totalPage", searchPageData.getTotalPages());
        resultMap.put("brandList", brandList);
        resultMap.put("categoryList", categoryList);

        return resultMap;
    }

}
