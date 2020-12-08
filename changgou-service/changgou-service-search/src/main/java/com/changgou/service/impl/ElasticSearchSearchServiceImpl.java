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


    @Override
    public Map<String, Object> search(Map<String, String> conditionMap) {
        Map<String,Object> stringMap=Maps.newHashMap();

        Map<String, Object> resultMap = new HashMap<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 判断map中是否又字段
        if (!StringUtils.isEmpty(conditionMap.get("keywords"))) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", conditionMap.get("keywords")).operator(Operator.AND));

        }

            // 组合查询调教

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
            if (!StringUtils.isEmpty(conditionMap.get("brand"))) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("brandName", conditionMap.get("brand")));
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

            if (!StringUtils.isEmpty(conditionMap.get("price"))) {
                String priceRange = conditionMap.get("price");

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

            int pageNum = 0;
            int pageSize = 20;
            // 判断字段中是否有currentPage和pageSize如果有则使用map中的值 如果没有或不为数字则使用默认值 也就是0 和 5
            if (StringUtils.isNumeric(conditionMap.get("pageNum"))) {
                pageNum = Integer.parseInt(conditionMap.get("pageNum"));
            }

            if (StringUtils.isNumeric(conditionMap.get("pageSize"))) {
                pageSize = Integer.parseInt(conditionMap.get("pageSize"));
            }

            // withPageable 为分页查询
            NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder
                    .withQuery(boolQueryBuilder)
                    .withPageable(PageRequest.of(pageNum, pageSize)).build();

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
                if (highlightFields != null && highlightFields.size() > 0) {
                    content.setName(highlightFields.get("name").get(0));
                }
            }

            SearchHits<SkuInfo> searchHits = skuInfoSearchPage.getSearchHits();

            List<SkuInfo> rows = new LinkedList<>();
        if (searchHits != null) {
            searchHits.forEach((skuInfoSearchHit -> {
                rows.add(skuInfoSearchHit.getContent());
            }));
        }


        resultMap.put("rows", rows);
            resultMap.put("total", skuInfoSearchPage.getTotalElements());
            resultMap.put("totalPage", skuInfoSearchPage.getTotalPages());
            resultMap.put("brandList", brandList);
            resultMap.put("categoryList", categoryList);
            resultMap.put("specList", specMap);
            resultMap.put("pageNum", pageNum);
            resultMap.put("pageSize", pageSize);

        return resultMap;
    }


    private NativeSearchQueryBuilder setSarchCondition(Map<String, Object> searchConditionMap,
                                                       NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        int currentPage = 0;
        if (searchConditionMap.get("currentPage") != null) {
            currentPage = Integer.parseInt((String) searchConditionMap.get("currentPage"));
        }

        int pageSize = 10;
        if (searchConditionMap.get("pageSize") != null) {
            pageSize = Integer.parseInt((String) searchConditionMap.get("pageSize"));
        }


        return nativeSearchQueryBuilder.withPageable(PageRequest.of(currentPage, pageSize));
    }

    /**
     * 分页查询
     * @param searchData 搜索数据
     * @param nativeSearchQueryBuilder
     * @return 分页对象
     */
    public SearchPage<SkuInfo> findPage(SearchHits<SkuInfo> searchData,
                                        NativeSearchQueryBuilder nativeSearchQueryBuilder) {


        return SearchHitSupport.searchPageFor(searchData, nativeSearchQueryBuilder.build().getPageable());
    }


    private void keyWordSearch(Map<String, Object> searchConditionMap, BoolQueryBuilder boolQueryBuilder, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String keywords = null;
        if (!StringUtils.isEmpty((String) searchConditionMap.get("keywords"))) {
            keywords = (String) searchConditionMap.get("keywords");
        }

        assert keywords != null;
        if (!StringUtils.isEmpty(keywords)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords));
        }

        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
    }

    private Map<String, Set<String>> specSearch(SearchHits<SkuInfo> searchHits) {
        Map<String, Set<String>> specMap = new HashMap<>();


        searchHits.getSearchHits().forEach((skuInfoSearchHit -> {
            Map<String, String> specJsonMap = JSON.parseObject(skuInfoSearchHit.getContent().getSpec(), Map.class);
            Set<String> specSet = null;

            for (String key : specJsonMap.keySet()) {
                if (specMap.containsKey(key)) {
                    specSet = specMap.get(key);
                } else {
                    specSet = new HashSet<>();
                }
                specSet.add(specJsonMap.get(key));
                specMap.put(key, specSet);
            }

        }));

        return specMap;
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

    private void setAggregation(String groupName, String fieldName, NativeSearchQueryBuilder nativeSearchQueryBuilder) {

        TermsAggregationBuilder brandAggregation = AggregationBuilders.terms(groupName).field(fieldName);
        nativeSearchQueryBuilder.addAggregation(brandAggregation);
    }

    public void mustSearch(String filed, String value, BoolQueryBuilder boolQueryBuilder) {
        if (value != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(filed, value));
        }

    }

    public Map<String, Object> searchTest(Map<String, Object> searchConditionMap) {

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        Map<String, Object> resultMap = new HashMap<>();

        keyWordSearch(searchConditionMap, boolQueryBuilder, nativeSearchQueryBuilder);

        String brandGroupName = "brandGroup";
        String categoryGroupName = "categoryGroup";

        // 因为每次调用都会向 nativeSearchQueryBuilder 中添加Aggregation 所有设置一个标识只调用一次方法

        setAggregation(brandGroupName, "brandName", nativeSearchQueryBuilder);
        setAggregation(categoryGroupName, "categoryName", nativeSearchQueryBuilder);



        setSarchCondition(searchConditionMap, nativeSearchQueryBuilder);
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();



        mustSearch("brandName", (String) searchConditionMap.get("brandName"), boolQueryBuilder);
        mustSearch("categoryName", (String) searchConditionMap.get("categoryName"), boolQueryBuilder);

        // todo 拼接查询
//        mustSpecSearch(searchConditionMap);

        SearchHits<SkuInfo> searchHits =
                elasticsearchRestTemplate.search(nativeSearchQuery, SkuInfo.class);

        List<String> brandList = polymerizationSearch(searchHits, brandGroupName);
        List<String> categoryList = polymerizationSearch(searchHits, categoryGroupName);

        SearchPage<SkuInfo> searchPageData = findPage(searchHits, nativeSearchQueryBuilder);


        Map<String, Set<String>> specMap =  specSearch(searchHits);



        resultMap.put("rows", searchPageData.getContent());
        resultMap.put("total", searchPageData.getTotalElements());
        resultMap.put("totalPage", searchPageData.getTotalPages());
        resultMap.put("brandList", brandList);
        resultMap.put("categoryList", categoryList);
        resultMap.put("specMap", specMap);

        return resultMap;
    }

}
