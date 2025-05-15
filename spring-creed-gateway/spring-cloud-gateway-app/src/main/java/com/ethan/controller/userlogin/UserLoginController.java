package com.ethan.controller.userlogin;

import com.ethan.controller.userlogin.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("user-info")
public class UserLoginController {

    @GetMapping("/status/{name}")
    public Mono<UserInfoVo> checkLoginStatus(ServerHttpRequest serverHttpRequest, @PathVariable("name") String name) {
        log.info("checkLoginStatus");
        return Mono.just(new UserInfoVo(name, false, true));
    }
}
