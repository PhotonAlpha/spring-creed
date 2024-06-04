package com.ethan.controller.product;

import com.ethan.controller.product.vo.ProductInfoVo;
import com.ethan.controller.recipient.vo.RecipientInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("product")
public class ProductController {
    @GetMapping("/info/{name}")
    public Mono<ProductInfoVo> getProductDetails(@PathVariable("name") String name) {
        log.info("getProductDetails");
        return Mono.just(new ProductInfoVo(name,"LENOVO", "phone", 100L, new BigDecimal("2000.00")));
    }
}
