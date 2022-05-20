package com.ethan.creedmall.member.feign;

import com.ethan.creedmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @className: CouponFeignService
 * @author: Ethan
 * @date: 16/3/2022
 **/
@FeignClient("creedmall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
