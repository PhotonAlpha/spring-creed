package com.ethan.controller.settlement;

import com.ethan.controller.settlement.vo.UserSettlementReqDto;
import com.ethan.controller.settlement.vo.UserSettlementVo;
import com.ethan.service.UserSettlementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
@RequestMapping("settlement")
public class UserSettlementController {
    @Resource
    private UserSettlementService userSettlementService;
    @PostMapping("/submit")
    public Mono<UserSettlementVo> submitRequest(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, @RequestBody UserSettlementReqDto dto) {
        log.info("submitRequest{}", dto);
        return userSettlementService.submitRequest(proxy, exchange, dto);
    }
}
