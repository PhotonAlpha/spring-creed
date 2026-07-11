package com.ethan.controller;

import com.ethan.controller.dto.ShoppingCatDTO;
import com.ethan.controller.dto.StudentDTO;
import org.apache.commons.lang3.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 27/3/24
 */
@RestController
public class GreetingsController {

    @GetMapping("/person/student/{id}")
    private Mono<StudentDTO> getInfo(@PathVariable("id") String id) {
        List<StudentDTO> list = Arrays.asList(
                new StudentDTO("1", "xiaomi", "male", 18),
                new StudentDTO("2", "xiaowang", "male", 20),
                new StudentDTO("3", "xiaohao", "male", 40)
        );
        return Mono.justOrEmpty(list.stream().filter(s -> Strings.CS.equals(id, s.getId())).findFirst());
    }

    @PostMapping("/person/product/{name}")
    private Flux<ShoppingCatDTO> getInfo2(@PathVariable("name") String name) {
        Flux<ShoppingCatDTO> shoppingCatFlux = Flux.fromIterable(Arrays.asList(
                new ShoppingCatDTO("1", "xiaomi", "iPhone", 2),
                new ShoppingCatDTO("1", "xiaomi", "Xiaomi14 Pro", 3),
                new ShoppingCatDTO("1", "xiaowang", "iPhone", 1),
                new ShoppingCatDTO("1", "xiaohao", "iPhone", 10),
                new ShoppingCatDTO("1", "xiaohao", "VIVO x100", 1),
                new ShoppingCatDTO("1", "xiaohao", "OPPO Find 90", 10)
        ));
        return shoppingCatFlux.filter(p -> Strings.CS.equals(p.getUsername(), name));
    }
}
