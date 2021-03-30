package com.changgou.controller;

import com.changgou.feign.CartFeign;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author rainbow
 * @date 2021/1/17
 */

@Controller
@RequestMapping("/wcart")
public class CartController {
    @Autowired
    private CartFeign cartFeign;


    @RequestMapping("/list")
    public String list(Model model) {
        Map<String, Object> resultMap = cartFeign.catdList("213");

        model.addAttribute("result", resultMap);

        return "cart";
    }

    @RequestMapping("/updateChecked")
    public String update(@RequestParam String skuId, @RequestParam Boolean checked) {
        System.out.println("skuId = " + skuId + ", checked = " + checked);

        return null;
    }
}
