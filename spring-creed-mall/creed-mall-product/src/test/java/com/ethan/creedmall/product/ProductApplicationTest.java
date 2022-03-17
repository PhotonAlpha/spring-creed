package com.ethan.creedmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ethan.creedmall.product.entity.BrandEntity;
import com.ethan.creedmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @className: ProductApplicationTest
 * @author: Ethan
 * @date: 16/3/2022
 **/
@SpringBootTest(classes = ProductApplication.class)
public class ProductApplicationTest {
    @Autowired
    private BrandService brandService;

    @Test
    void testService() {
        // BrandEntity brandEntity = new BrandEntity();
        // brandEntity.setBrandId(1L);
        // brandEntity.setLogo("noop");
        // brandEntity.setDescript("手机品牌");
        // brandEntity.setName("HUAWEI");
        // boolean success = brandService.updateById(brandEntity);
        // System.out.println("is success:" + success);

        List<BrandEntity> brandEntities = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));

        System.out.println(brandEntities);
    }
}
