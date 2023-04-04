/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.reactive.controller;

import com.ethan.reactive.dal.entity.permission.MenuDO;
import com.ethan.reactive.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/system/auth")
public class AuthController {
    @Resource
    private MenuService menuService;

    @GetMapping("/{id}")
    public Mono<MenuDO> getEmployeeById(@PathVariable Long id) {
        return Mono.just(menuService.getMenu(id));
    }

    @GetMapping("/all")
    public Flux<MenuDO> getAllEmployees() {
        return Flux.just(menuService.getMenus().toArray(MenuDO[]::new));
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('USER')")
    public Mono<String> greetAdmin(Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(name -> String.format("Admin access: %s", name));
    }


    @GetMapping(path = "/users/{name}")
    public Mono<String> getName(@PathVariable String name) {
        return Mono.just(name);
    }
    @GetMapping(path = "/exception")
    public Mono<String> exception(@PathVariable String name) {
        int i = 1 / 0;
        return Mono.just(name);
    }



}
