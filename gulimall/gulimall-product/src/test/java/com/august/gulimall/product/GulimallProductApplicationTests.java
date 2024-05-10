package com.august.gulimall.product;

import com.august.gulimall.product.dao.BrandDao;
import com.august.gulimall.product.dto.BrandDTO;
import com.august.gulimall.product.entity.BrandEntity;
import com.august.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
class GulimallProductApplicationTests {

	@Resource
	BrandDao brandDao;


	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Test
	public void testRedis() {
		stringRedisTemplate.opsForValue().set("hello", "world");
		System.out.println(stringRedisTemplate.opsForValue().get("hello"));
	}



	@Test
	public void test() {
		brandDao.insert(new BrandEntity(null, "华为", "华为", "华为", 0, null, 0,0));
	}

}
