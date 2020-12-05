package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public void insert(Brand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void deleteById(Integer brandId) {
        brandMapper.deleteByPrimaryKey(brandId);
    }

    @Override
    public Page<Brand> findPage(Map<String, String> searchConditionMap, Integer pageNumber, Integer pageSize) {
        // 开始分页
        PageHelper.startPage(pageNumber, pageSize);

        // 查询条件
        Example example = new Example(Brand.class);

        // 构建查询对象
        Example.Criteria criteria = example.createCriteria();

        if (searchConditionMap != null && searchConditionMap.size() > 0) {
            if (StringUtils.isNotEmpty(searchConditionMap.get("name"))) {
                // 模糊查询
                criteria.andLike("name", "%" + searchConditionMap.get("name") + "%");
            }

            if (StringUtils.isNotEmpty("letter")) {
                // 全职对比
                criteria.andEqualTo("letter", searchConditionMap.get("letter"));
            }
        }

        // 条件查询
        return (Page<Brand>) brandMapper.selectByExample(example);
    }

    @Override
    public List<Brand> findBrandByAtName(String cateName) {
        return brandMapper.findBrandByCategoryName(cateName);
    }

}
