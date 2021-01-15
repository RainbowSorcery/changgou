package com.changgou.service.impl;

import com.changgou.goods.entity.Constants;
import com.changgou.goods.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.pojo.OrderItem;
import com.changgou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Resource
    private RedisTemplate<String, OrderItem> redisTemplate;

    @Override
    public void addCard(String username, String skuId, Integer num) {
        // 先从redis中取数据如果有数据 则不用擦查询
        // 查找redis中是否有数据
        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(Constants.REDIS_CART + username).get(skuId);

        // 判断redis中 是否查找到当前用户购物车列表 如果购物车中有该商品 将商品数量更新 总价格更
        if (orderItem != null) {
            orderItem.setNum(orderItem.getNum() + num);

            // 如果前端传入商品数量为负数 删除购物车内容
            if (orderItem.getNum() >= 0) {
                redisTemplate.boundHashOps(Constants.REDIS_CART).delete(skuId);

                return;
            }

            orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());

            orderItem.setPayMoney(orderItem.getMoney() * orderItem.getNum());
        } else {
            orderItem = builderOrderItem(skuId, num);

            if (orderItem != null) {
                redisTemplate.boundHashOps(Constants.REDIS_CART + username).put(skuId, orderItem);
            }
        }

    }

    @Override
    public Map<String, Object> cardList(String username) {
        Map<String, Object> resultMap = new HashMap<>();
        // 想redis中查询数据
        List<Object> cardList = redisTemplate.boundHashOps(Constants.REDIS_CART + username).values();

        Integer total = 0;
        Integer price = 0;

        if (cardList != null && cardList.size() > 0) {
            for (Object cardObject : cardList) {
                OrderItem orderItem = (OrderItem) cardObject;

                // total为被选中的数值 isChecked如果为true total + 1
                // price为被选中的价格数
                if (orderItem.isChecked()) {
                    total++;
                    price += orderItem.getMoney();
                }
            }
        }

        resultMap.put("total", total);
        resultMap.put("totalPrice", price);
        resultMap.put("orderItemList", cardList);

        return resultMap;
    }

    @Override
    public void deleteCart(String username, String skuId) {
        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(Constants.REDIS_CART + username).get(skuId);

        if (orderItem != null) {
            redisTemplate.boundHashOps(Constants.REDIS_CART + username).delete(skuId);
        }
    }

    @Override
    public void updateChecked(String username, String skuId, boolean checked) {
        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(Constants.REDIS_CART + username).get(skuId);

        if (orderItem != null) {
            orderItem.setChecked(checked);
            redisTemplate.boundHashOps(Constants.REDIS_CART + username).put(skuId, orderItem);
        }

    }

    private OrderItem builderOrderItem(String skuId, Integer num) {
        Result<Sku> skuResult = skuFeign.findSkuById(skuId);
        if (skuResult.isFlag() && skuResult.getData() != null) {
            Sku sku = skuResult.getData();
            Result<Goods> spuResult = spuFeign.findGoodsBySpuId(sku.getSpuId());
            Spu spu = spuResult.getData().getSpu();

            if (spuResult.isFlag() && spuResult.getData() != null) {
                OrderItem orderItem = new OrderItem();
                orderItem.setChecked(false);
                orderItem.setCategoryId1(spu.getCategory1Id());
                orderItem.setCategoryId2(spu.getCategory2Id());
                orderItem.setCategoryId3(spu.getCategory3Id());
                orderItem.setSpuId(spu.getId());
                orderItem.setSkuId(sku.getId());
                orderItem.setPrice(sku.getPrice());
                orderItem.setNum(num);
                orderItem.setMoney(sku.getPrice() * num);
                orderItem.setPayMoney(sku.getPrice() * num);
                orderItem.setWeight(sku.getWeight());
                orderItem.setImage(sku.getImage());

                return orderItem;
            }

        }
        return null;
    }



}
