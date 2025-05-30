package com.ethan.controller;

import com.ethan.controller.dto.ShoppingCatDTO;
import com.ethan.controller.dto.StudentDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 27/3/24
 */
@RestController
@Slf4j
public class GreetingsController {
    @Value("${server.port:}")
    private int port;

    @GetMapping("/proxy/showAll")
    public Mono<ResponseEntity<?>> showAll(ProxyExchange<byte[]> proxy) {
        // String path = proxy.path("/proxy/path/");
        // System.out.println("path:" + path);
        // ServerHttpRequest request = exchange.getRequest();
        // String currentPath = request.getURI().toString().replaceAll(request.getPath().value(), "");

        String currentPath = "";
        Mono<ResponseEntity<String>> firstApi = proxy.uri(currentPath + "/mono/showMono").get(o -> {
            byte[] body = o.getBody();
            return ResponseEntity.ok(new String(body, StandardCharsets.UTF_8));
        });
        Mono<ResponseEntity<String>> secondApi = proxy.uri(currentPath + "/mono/showMono2").get(o -> {
            byte[] body = o.getBody();
            return ResponseEntity.ok(new String(body, StandardCharsets.UTF_8));
        });
        ObjectMapper mapper = new ObjectMapper();

        // Flux<ResponseEntity<String>> flux = Flux.merge(firstApi, secondApi);
        Mono.zip(firstApi, secondApi)
                .map(a -> a.getT1());

        // Flux.merge(firstApi, secondApi).

        return firstApi.zipWith(secondApi)
                .map(tuple -> {
                    ResponseEntity<String> t1 = tuple.getT1();
                    String body = t1.getBody();
                    JsonNode jsonNode1;
                    try {
                        jsonNode1 = mapper.readTree(body);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    ObjectReader reader = mapper.readerForUpdating(jsonNode1);
                    ResponseEntity<String> t2 = tuple.getT2();
                    body = t2.getBody();
                    JsonNode jsonNode2;
                    try {
                        jsonNode2 = mapper.readTree(body);
                        jsonNode2 = reader.readValue(jsonNode2);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return ResponseEntity.ok(jsonNode2);
                });
        // return Mono.just(ResponseEntity.ok(""));
    }

    @GetMapping("/person/student/{id}")
    private Mono<StudentDTO> getInfo(@PathVariable("id") String id) {
        log.info("student:{}", id);
        List<StudentDTO> list = Arrays.asList(
                new StudentDTO("1", "xiaomi", "male", 18),
                new StudentDTO("2", "xiaowang", "male", 20),
                new StudentDTO("3", "xiaohao", "male", 40)
        );
        Consumer<StudentDTO> c = s -> s.setServer(port + "");
        list.forEach(c);
        return Mono.justOrEmpty(list.stream().filter(s -> StringUtils.equals(id, s.getId())).findFirst());
    }

    @GetMapping("/person/product/{name}")
    private Flux<ShoppingCatDTO> getInfo2(@PathVariable("name") String name) {
        log.info("product:{}", name);
        Flux<ShoppingCatDTO> shoppingCatFlux = Flux.fromIterable(Arrays.asList(
                new ShoppingCatDTO("1", "xiaomi", "iPhone", 2),
                new ShoppingCatDTO("1", "xiaomi", "Xiaomi14 Pro", 3),
                new ShoppingCatDTO("1", "xiaowang", "iPhone", 1),
                new ShoppingCatDTO("1", "xiaohao", "iPhone", 10),
                new ShoppingCatDTO("1", "xiaohao", "VIVO x100", 1),
                new ShoppingCatDTO("1", "xiaohao", "OPPO Find 90", 10)
        ));
        return shoppingCatFlux.filter(p -> StringUtils.equals(p.getUsername(), name));
    }

    @GetMapping("/fallback/person/student/{id}")
    private Mono<StudentDTO> getFallbackInfo(@PathVariable("id") String id) {
        log.info("fallback for student:{}", id);
        return Mono.justOrEmpty(new StudentDTO("-1", "unknow", "unknow", -1));
    }
    @GetMapping("/fallback/person/product/{name}")
    private Flux<ShoppingCatDTO> getInfoFallback2(@PathVariable("name") String name) {
        log.info("fallback for product:{}", name);
        return Flux.just(new ShoppingCatDTO("-1", "unknow", "NA", -1));
    }
}
