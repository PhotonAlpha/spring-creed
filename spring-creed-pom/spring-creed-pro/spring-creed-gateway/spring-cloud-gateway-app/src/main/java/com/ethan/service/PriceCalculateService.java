package com.ethan.service;

import com.ethan.controller.activity.activity.ActivityInfoVo;
import com.ethan.controller.coupon.coupon.CouponVo;
import com.ethan.controller.invoice.invoice.InvoiceInfoVo;
import com.ethan.controller.price.price.GoodsInfoDto;
import com.ethan.controller.price.price.PriceInfoVo;
import com.ethan.controller.product.vo.ProductInfoVo;
import com.ethan.controller.recipient.vo.RecipientInfoVo;
import com.ethan.controller.userlogin.vo.UserInfoVo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple5;

import java.util.function.Function;

@Service
@Slf4j
public class PriceCalculateService {
    public Mono<ResponseEntity<PriceInfoVo>> getPriceCalculate(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, GoodsInfoDto goods) {
        log.info("getPriceCalculate:{}", goods);
        ProxyExchangeResolver<String, Mono<ResponseEntity<PriceInfoVo>>> resolver = ProxyExchangeResolver.post(goods);
        return resolver.apply(proxy, exchange, "/price/info");
    }

    public Function<Tuple5<Tuple2<UserInfoVo, ProductInfoVo>, RecipientInfoVo, InvoiceInfoVo, ActivityInfoVo, CouponVo> , Mono<GoodsInfoDto>> convert() {
        return result ->
            Mono.just(
                new GoodsInfoDto(result.getT1().getT1(), result.getT1().getT2(), result.getT2(), result.getT3(), result.getT4(), result.getT5())
            );
    }
}
