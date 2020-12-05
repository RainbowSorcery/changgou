package com.changgou.goods.controller;

import com.changgou.goods.entity.PageResult;
import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/findAll")
    public Result<Brand> findAll() {
        List<Brand> allBrand = brandService.findAll();


        return new Result<>(true, StatusCode.OK, "查询全部品牌成功.", allBrand);
    }

    @GetMapping("/{brandId}")
    public Result<Brand> findById(@PathVariable Integer brandId) {
        Brand brand = brandService.findById(brandId);

        return new Result<Brand>(true, StatusCode.OK, "查询品牌成功", brand);
    }

    @PostMapping
    public Result<Brand> insertBrand(@RequestBody Brand brand) {
        brandService.insert(brand);

        return new Result<>(true, StatusCode.OK, "品牌添加成功");
    }

    @PutMapping
    public Result<Brand> updateBrand(@RequestBody Brand brand) {
        brandService.update(brand);

        return new Result<>(true, StatusCode.OK, "品牌修改成功");
    }

    @DeleteMapping("/{brandId}")
    public Result<Brand> deleteBrandById(@PathVariable Integer brandId) {
        brandService.deleteById(brandId);

        return new Result<>(true, StatusCode.OK, "品牌删除成功");
    }

    // @RequestParam 用于url中的key和value 转换为map 如 http://www.baidu.com?id=1&name=jck 转换为map id 为key 1 为value name 为key  jck 为value
    @GetMapping("/searchPage/{pageNumber}/{pageSize}")
    public Result<Brand> findPage(@RequestParam Map<String, String> searchConditionMap,
                                  @PathVariable Integer pageNumber, @PathVariable Integer pageSize) {

        Page<Brand> brandPage = brandService.findPage(searchConditionMap, pageNumber, pageSize);

        // 获取分页结果
        PageResult<Brand> brandPageResult = new PageResult<>(brandPage.getTotal(), brandPage.getResult());

        // 将分页结果添加至统一返回结果
        return new Result<>(true, StatusCode.OK, "分页查询成功", brandPageResult);
    }

    @GetMapping("/cate/{categoryName}")
    public Result<Brand> findBrandByCategryName(@PathVariable String categoryName) {

        List<Brand> brandByAtName = brandService.findBrandByAtName(categoryName);

        return new Result<>(true, StatusCode.OK, "根据分类名称查询品牌列表成功.", brandByAtName);
    }
}
