package com.ethan.exchange.client;

import com.ethan.validator.controller.vo.MyAccountDetailsVO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Map;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/3/25
 */
@HttpExchange("http://localhost:8088/buziVa/artisan")
public interface ArtisanClientX {
    @PostExchange("list")
    ResponseEntity<List<MyAccountDetailsVO>> listUser(@RequestBody MyAccountDetailsVO user, @RequestHeader HttpHeaders httpHeaders, @RequestParam Map<String, String> params);
}
