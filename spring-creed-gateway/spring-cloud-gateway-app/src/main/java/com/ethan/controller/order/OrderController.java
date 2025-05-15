package com.ethan.controller.order;

import com.ethan.controller.coupon.coupon.CouponVo;
import com.ethan.controller.order.order.OrderReqDto;
import com.ethan.controller.order.order.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("order")
public class OrderController {
    @PostMapping("/submit")
    public Mono<OrderVo> getOrderDetails(@RequestBody OrderReqDto dto) {
        log.info("getOrderDetails:{}", dto);
        return Mono.just(new OrderVo(UUID.randomUUID().toString(), dto.getUserInfo().getName(), dto.getProductInfo().getProductName()));
    }
}
