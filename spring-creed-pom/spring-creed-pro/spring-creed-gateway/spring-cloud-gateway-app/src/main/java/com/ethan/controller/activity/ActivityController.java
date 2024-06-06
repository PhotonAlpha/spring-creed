package com.ethan.controller.activity;

import com.ethan.controller.activity.activity.ActivityInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("activity")
public class ActivityController {

    @GetMapping("/info/{name}")
    public Mono<ActivityInfoVo> getInvoiceInfo(@PathVariable("name") String name) {
        log.info("getInvoiceInfo");
        return Mono.just(new ActivityInfoVo(name, "5"));
    }
}
