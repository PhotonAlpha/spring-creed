package com.ethan.controller.coupon;

import com.ethan.controller.coupon.coupon.CouponVo;
import com.ethan.controller.invoice.invoice.InvoiceInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("coupon")
public class CouponController {
    @GetMapping("/info/{name}")
    public Mono<CouponVo> getProductDetails(@PathVariable("name") String name) {
        log.info("getProductDetails");
        return Mono.just(new CouponVo(name, new BigDecimal("5.00")));
    }
}
