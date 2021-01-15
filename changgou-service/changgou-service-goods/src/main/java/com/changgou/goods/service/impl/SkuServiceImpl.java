package com.changgou.goods.service.impl;

import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuMapper skuMapper;

    @Override
    public Sku findSkuById(String skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }

    @Override
    public List<Sku> findBySpuId(String spuId) {
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spuId);

        return skuMapper.selectByExample(example);
    }

    @Override
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }
}
