package com.ethan.controller.recipient;

import com.ethan.controller.recipient.vo.RecipientInfoVo;
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
@RequestMapping("recipient")
public class RecipientController {
    @GetMapping("/recipient/{name}")
    public Mono<RecipientInfoVo> getRecipientDetails(@PathVariable("name") String name) {
        log.info("getRecipientDetails");
        return Mono.just(new RecipientInfoVo(name, "80848888", "CN", "northpoint"));
    }
}
