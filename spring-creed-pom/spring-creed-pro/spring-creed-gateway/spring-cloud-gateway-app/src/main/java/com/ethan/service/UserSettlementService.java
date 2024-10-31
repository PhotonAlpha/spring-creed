package com.ethan.service;

import com.ethan.controller.activity.activity.ActivityInfoVo;
import com.ethan.controller.coupon.coupon.CouponVo;
import com.ethan.controller.invoice.invoice.InvoiceInfoVo;
import com.ethan.controller.order.order.OrderReqDto;
import com.ethan.controller.order.order.OrderVo;
import com.ethan.controller.product.vo.ProductInfoVo;
import com.ethan.controller.recipient.vo.RecipientInfoVo;
import com.ethan.controller.settlement.vo.UserSettlementReqDto;
import com.ethan.controller.settlement.vo.UserSettlementVo;
import com.ethan.controller.userlogin.vo.UserInfoVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuple5;

@Service
@Slf4j
public class UserSettlementService {
    @Resource
    private UserLoginService userLoginService;
    @Resource
    private ProductService productService;
    @Resource
    private OrderService orderService;
    @Resource
    private RecipientService recipientService;
    @Resource
    private InvoiceService invoiceService;
    @Resource
    private ActivityService activityService;
    @Resource
    private CouponService couponService;
    @Resource
    private PriceCalculateService priceCalculateService;

    public Mono<UserSettlementVo> submitRequest(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, UserSettlementReqDto dto) {
        return userLoginService.getUserInfo(proxy, exchange, dto.getUserName())
                .filter(userLoginService.hasLogin())
                .zipWith(productService.getProductInfo(proxy, exchange, dto.getUserName()))
                .filter(res -> productService.productInStock().test(dto.getStock(), res.getT2()))
                // proceed submit flow
                .flatMap(res ->
                        Flux.zip(
                                        Mono.just(res),
                                        recipientService.getRecipientInfo(proxy, exchange, dto.getUserName()),
                                        invoiceService.getInvoiceInfo(proxy, exchange, dto.getUserName()),
                                        activityService.getActivityInfo(proxy, exchange, dto.getUserName()),
                                        couponService.getCoupon(proxy, exchange, dto.getUserName())
                                ).flatMap(priceCalculateService.convert())
                                .single()
                )
                .flatMap(goods ->
                        Flux.zip(
                                Mono.just(goods),
                                priceCalculateService.getPriceCalculate(proxy, exchange, goods)
                            )
                            .single()

                )
                .flatMap(comb ->
                    orderService.payment(proxy, exchange,
                            new OrderReqDto(
                                    comb.getT1().getUserInfo(),
                                    comb.getT1().getProductInfo(),
                                    comb.getT1().getRecipientInfo(),
                                    comb.getT1().getInvoiceInfo(),
                                    comb.getT1().getActivityInfo(),
                                    comb.getT1().getCoupon(),
                                    comb.getT2()
                                    ))
                )
                // .onErrorResume()
                .flatMap(comb -> this.success(comb.getUserName(), comb.getOrderNo()))
                .switchIfEmpty(Mono.defer(() -> this.fallback(dto.getUserName())));
    }

    private Mono<UserSettlementVo> fallback(String name) {
        return Mono.just(new UserSettlementVo(name, "NA",false));
    }
    private Mono<UserSettlementVo> success(String name, String orderNumber) {
        return Mono.just(new UserSettlementVo(name,orderNumber, true));
    }


}
