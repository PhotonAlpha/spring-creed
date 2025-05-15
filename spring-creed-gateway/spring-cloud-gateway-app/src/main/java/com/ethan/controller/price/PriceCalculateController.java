package com.ethan.controller.price;

import com.ethan.controller.activity.activity.ActivityInfoVo;
import com.ethan.controller.coupon.coupon.CouponVo;
import com.ethan.controller.invoice.invoice.InvoiceInfoVo;
import com.ethan.controller.price.price.GoodsInfoDto;
import com.ethan.controller.price.price.PriceInfoVo;
import com.ethan.controller.product.vo.ProductInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("price")
public class PriceCalculateController {
    @PostMapping("/info")
    public Mono<PriceInfoVo> getPriceDetails(@RequestBody GoodsInfoDto dto) {
        log.info("getPriceDetails");
        ProductInfoVo productInfo = dto.getProductInfo();
        ActivityInfoVo activityInfo = dto.getActivityInfo();
        CouponVo coupon = dto.getCoupon();

        String discountPercentage = activityInfo.getDiscountPercentage();
        BigDecimal discount = Optional.ofNullable(coupon).map(CouponVo::getDiscount).orElse(BigDecimal.ZERO);

        Long stock = productInfo.getStock();
        BigDecimal price = productInfo.getPrice();
        BigDecimal totalPrice = price.multiply(new BigDecimal(stock))
                .multiply(new BigDecimal("1").subtract(new BigDecimal(discountPercentage).movePointLeft(2)))
                .subtract(discount);

        return Mono.just(new PriceInfoVo(productInfo.getProductName(), totalPrice));
    }
}
