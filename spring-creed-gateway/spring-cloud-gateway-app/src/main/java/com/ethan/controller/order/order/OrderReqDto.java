package com.ethan.controller.order.order;

import com.ethan.controller.activity.activity.ActivityInfoVo;
import com.ethan.controller.coupon.coupon.CouponVo;
import com.ethan.controller.invoice.invoice.InvoiceInfoVo;
import com.ethan.controller.price.price.PriceInfoVo;
import com.ethan.controller.product.vo.ProductInfoVo;
import com.ethan.controller.recipient.vo.RecipientInfoVo;
import com.ethan.controller.userlogin.vo.UserInfoVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReqDto {
    private UserInfoVo userInfo;
    private ProductInfoVo productInfo;
    private RecipientInfoVo recipientInfo;
    private InvoiceInfoVo invoiceInfo;
    private ActivityInfoVo activityInfo;
    private CouponVo coupon;
    private PriceInfoVo priceInfoVo;

}
