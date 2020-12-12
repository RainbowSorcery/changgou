package com.changgou.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.entity.Result;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.service.PageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class PageServiceImpl implements PageService {
    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${pagePath}")
    private String pagePath;

    @Override
    public Map<String, Object> buildPageData(String spuId) {
        Map<String, Object> resultMap = new HashMap<>();

        if (!StringUtils.isEmpty(spuId)) {
            Result<Goods> spuResult = spuFeign.findGoodsBySpuId(spuId);

            Goods spuResultData = spuResult.getData();

            if (!spuResult.isFlag() || spuResultData == null) {
                throw new RuntimeException("数据不存在");
            }

            Spu spu = spuResultData.getSpu();

            List<Sku> skuList = spuResultData.getSkuList();


            Integer category1Id = spu.getCategory1Id();
            Integer category2Id = spu.getCategory2Id();
            Integer category3Id = spu.getCategory3Id();

            Category category1 = categoryFeign.findCategoryById(category1Id).getData();
            Category category2 = categoryFeign.findCategoryById(category2Id).getData();
            Category category3 = categoryFeign.findCategoryById(category3Id).getData();

            resultMap.put("category1", category1);
            resultMap.put("category2", category2);
            resultMap.put("category3", category3);

            resultMap.put("spu", spu);
            resultMap.put("skuList", skuList);

            List<String> imageList = new ArrayList<>();

            List<Map> imagesList = JSON.parseArray(spu.getImages(), Map.class);
            if (imagesList != null && imagesList.size() > 0) {
                imagesList.forEach((images) -> {
                    String url = (String) images.get("url");
                    imageList.add(url);
                });
            }


            String specItemsString = spu.getSpecItems();

            Map specJsonMap = JSON.parseObject(specItemsString, Map.class);

            resultMap.put("imageList", imageList);
            resultMap.put("spuData", spuResultData);
            resultMap.put("specificationList", specJsonMap);
        }

        return resultMap;
    }

    @Override
    public void createPageHtml(String spuId) {
        Map<String, Object> contextData = this.buildPageData(spuId);

        Context context = new Context();
        context.setVariables(contextData);

        File pageFile = new File(pagePath);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(pageFile + "/" + spuId + ".html");
            templateEngine.process("item", context, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!pageFile.exists()) {
            pageFile.mkdirs();
        }

    }
}
