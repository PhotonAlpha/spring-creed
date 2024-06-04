package com.ethan.service;

import com.ethan.controller.product.vo.ProductInfoVo;
import com.ethan.controller.settlement.vo.UserSettlementReqDto;
import com.ethan.controller.userlogin.vo.UserInfoVo;
import com.ethan.dal.entity.ProductInfo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.naming.Name;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Service
@Slf4j
public class ProductService {
    public Mono<ProductInfoVo> getProductInfo(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, String name) {
        ProxyExchangeResolver<String, Mono<ProductInfoVo>> productResolver = ProxyExchangeResolver.get();
        return productResolver.apply(proxy, exchange, "/product/info/" + name);
    }

    public BiPredicate<Long, ProductInfoVo> productInStock() {
        return (stock, resp) ->
                 Objects.nonNull(resp)
                    && resp.getStock() <= stock;
    }
}
