package com.ethan.example.controller;

import com.ethan.example.controller.vo.ArtisanDetailsVO;
import com.ethan.example.service.ArtisanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

import static com.ethan.example.service.impl.ArtisanServiceImpl.RANDOM_INDEX;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@RestController
@RequestMapping("/buziVa/artisan")
@Slf4j
public class ArtisanController {
    @Resource
    ArtisanService artisanService;
    private static final Random random = new Random();

    /**
     * 测试resilience4j-retry功能
     * @param user
     * @return
     */
    @PostMapping("query")
    public ArtisanDetailsVO queryUser(@RequestBody ArtisanDetailsVO user) {
        int i = random.nextInt(9);
        log.info("retry times:{} queryUser:{} ", i, user);
        RANDOM_INDEX.set(i);

        return artisanService.findByIdOrName(user.getId(), user.getName());
    }
    @GetMapping("list")
    public List<ArtisanDetailsVO> listUser() {
        log.info("listUser");
        return artisanService.findAll();
    }

    /**
     * 测试Spring cloud load balancer
     * @return
     */
    @GetMapping("remote-list")
    public List<ArtisanDetailsVO> remoteListUser() {
        log.info("listUser");
        return artisanService.findAllRemote();
    }


}
