package com.changgou.goods.service;



import com.changgou.goods.pojo.Spec;

import java.util.List;

public interface SpecService {
    List<Spec> findByCategoryNameBySpec(String categoryName);
}
