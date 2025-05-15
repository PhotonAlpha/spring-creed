package com.ethan.controller.invoice;

import com.ethan.controller.invoice.invoice.InvoiceInfoVo;
import com.ethan.controller.product.vo.ProductInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("invoice")
public class InvoiceController {
    @GetMapping("/info/{name}")
    public Mono<InvoiceInfoVo> getInvoiceDetails(@PathVariable("name") String name) {
        log.info("getInvoiceDetails");
        return Mono.just(new InvoiceInfoVo(name, UUID.randomUUID().toString(), "COMPANY", "yiiiiiiii"));
    }
}
