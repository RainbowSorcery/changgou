package com.changgou;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ChanggouServiceSearchApplicationTests {
	@Autowired
	private SkuFeign skuFeign;

	@Test
	void contextLoads() {
	}

	@Test
	public void skuFeignTest() {
		List<Sku> spuIdBySku = skuFeign.findSpuIdBySku("10000005620800");

		spuIdBySku.forEach((System.out::println));
	}

}
