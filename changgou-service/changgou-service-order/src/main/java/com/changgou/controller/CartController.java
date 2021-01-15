package com.changgou.controller;

import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.table.TableRowSorter;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<String> addCart(@RequestParam("username") String username, @RequestParam("skuId") String skuId, @RequestParam("num") Integer num) {
        cartService.addCard(username, skuId, num);

        return new Result<>(true, StatusCode.OK, "添加购物车成功.");
    }

    @GetMapping("/list")
    public Map<String, Object> catdList(@RequestParam("username") String username) {

        return cartService.cardList(username);
    }

    @DeleteMapping("/delete")
    public Result<String> deleteCart(@RequestParam("username") String username,
                                     @RequestParam("skuId") String skuId) {
        cartService.deleteCart(username, skuId);

        return new Result<>(true, StatusCode.OK, "删除购物车成功.");
    }

    @PutMapping("/updateChecked")
    public Result<String> updateChecked(@RequestParam("username") String username,
                                        @RequestParam("checked") boolean checked,
                                        @RequestParam("skuId") String skuId) {
        cartService.updateChecked(username, skuId, checked);

        return new Result<>(true, StatusCode.OK, "更新商品被选中状态成功.");

    }
}
