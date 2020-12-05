package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    @Select("SELECT * FROM tb_brand tb INNER JOIN tb_category_brand tcb ON tcb.brand_id = tb.id INNER JOIN tb_category tc ON tc.id = tcb.category_id AND tc.`name` = #{categoruName};")
    List<Brand> findBrandByCategoryName(@Param("categoruName") String categoruName);


}
