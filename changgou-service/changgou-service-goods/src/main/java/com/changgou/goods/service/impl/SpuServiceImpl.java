package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.utils.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 操作多个表一定要配置事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addGoods(Goods goods) {
        Spu spu = goods.getSpu();
        // 设置spu id
        spu.setId(String.valueOf(idWorker.nextId()));
        // 插入spu
        spuMapper.insertSelective(spu);

        // 保存SKU
        List<Sku> skuList = goods.getSkuList();


        saveSku(spu, skuList);
    }

    private void saveSku(Spu spu, List<Sku> skuList) {
        if (skuList != null && skuList.size() > 0) {

            Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());

            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());

            if (brand != null && category != null) {
                CategoryBrand categoryBrand = new CategoryBrand();
                categoryBrand.setBrandId(spu.getBrandId());
                categoryBrand.setCategoryId(spu.getCategory3Id());
                int categoryCount = categoryBrandMapper.selectCount(categoryBrand);

                if (categoryCount == 0) {
                    categoryBrandMapper.insertSelective(categoryBrand);
                }
            }

            skuList.forEach((sku -> {
                sku.setId(String.valueOf(idWorker.nextId()));

                if (StringUtils.isEmpty(sku.getSpec())) {
                    sku.setSpec("{}");
                }


                StringBuilder spec = new StringBuilder(sku.getSpec());
                Map<String, String> specMap = JSON.parseObject(spec.toString(), Map.class);

                for (String key : specMap.keySet()) {
                    spec.append(specMap.get(key));
                }

                sku.setName(spec.toString());
                sku.setCreateTime(new Date());
                sku.setUpdateTime(new Date());
                sku.setSpuId(spu.getId());
                if (category != null) {
                    sku.setCategoryId(category.getId());
                    sku.setCategoryName(category.getName());
                }
                if (brand != null) {
                    sku.setBrandName(brand.getName());
                }

                skuMapper.insertSelective(sku);
            }));
        }
    }

    @Override
    public Goods findGoodsBySpuId(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spuId);

        List<Sku> skus = skuMapper.selectByExample(example);

        Goods goods = new Goods();
        goods.setSkuList(skus);
        goods.setSpu(spu);


        return goods;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Goods goods) {
        Spu spu = goods.getSpu();

        spuMapper.updateByPrimaryKey(spu);

        List<Sku> skuList = goods.getSkuList();

        String spuId = spu.getId();

        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spuId);

        skuMapper.deleteByExample(example);


        saveSku(spu, skuList);
    }

    @Override
    public void aduitGoods(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        spu.setStatus("1");

        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void upGoods(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if (!"1".equals(spu.getStatus())) {
            throw new RuntimeException("商品审核未通过.");
        }

        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
        
        // 将spuID 存入至rabbitMQ
        rabbitTemplate.convertAndSend("goods_up_exchange",  null, spuId);
    }

    @Override
    public void downGoods(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if (!"1".equals(spu.getIsMarketable())) {
            throw new RuntimeException("商品未上架.");
        }

        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);

        // 将下架商品的spuID存入至RabbitMQ
        rabbitTemplate.convertAndSend("goods_donw_exchange", null, spuId);
    }

    @Override
    public void deleteLogic(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if ("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("商品已逻辑删除.");
        }

        if ("1".equals(spu.getIsMarketable())) {
            throw new RuntimeException("上架中商品无法删除.");
        }

        spu.setIsDelete("1");

        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void resotre(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if ("0".equals(spu.getIsDelete())) {
            throw new RuntimeException("商品未逻辑删除，无法恢复.");
        }

        spu.setIsDelete("0");

        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGoods(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spuId);

        skuMapper.deleteByExample(example);

        // todo 不小心删除了库(会不会跑路.)
        spuMapper.deleteByPrimaryKey(spu);
    }
}