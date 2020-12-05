package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * 品牌service
 * @author 36537
 */
public interface BrandService {
    /**
     * 查询品牌列表
     * @return 品牌列表
     */
    List<Brand> findAll();

    /**
     * 根据id查询品牌
     * @param brandId 品牌id
     * @return 查询到的品牌
     */
    Brand findById(Integer brandId);

    /**
     * 新增品牌
     * @param brand 品牌对象
     */
    void insert(Brand brand);

    /**
     * 更新品牌信息
     * @param brand 品牌对象
     */
    void update(Brand brand);

    /**
     * 根据品牌id删除品牌
     * @param brandId 品牌id
     */
    void deleteById(Integer brandId);


    /**
     * 分页查询
     * @param searchConditionMap 查询条件
     * @param pageNumber 页码
     * @param pageSize 每页显示条数
     * @return 分页对象
     */
    Page<Brand> findPage(Map<String, String> searchConditionMap, Integer pageNumber, Integer pageSize);


    /**
     * 根据名称查类别
     * @param cateName 分类名称
     * @return 类别
     */
    List<Brand> findBrandByAtName(String cateName);
}
