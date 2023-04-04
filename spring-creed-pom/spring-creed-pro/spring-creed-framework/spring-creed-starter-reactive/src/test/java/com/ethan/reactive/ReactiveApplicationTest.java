/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.reactive;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
public class ReactiveApplicationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenUserNameIsBaeldung_thenWebFilterIsApplied() {
        EntityExchangeResult<String> result = webTestClient
                .get()
                .uri("/system/auth/users/baeldung")
                .headers(headers -> headers.setBasicAuth("user", "user"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();
        assertEquals(result.getResponseBody(), "baeldung");
        assertEquals(
                result.getResponseHeaders().getFirst("web-filter"),
                "web-filter-test");
    }

    @Test
    public void whenUserNameIsTest_thenHandlerFilterFunctionIsNotApplied() {
        webTestClient.get().uri("/system/auth/users/test")
                .headers(headers -> headers.setBasicAuth("user", "user"))
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void testExceptionHandling() {
        webTestClient.get().uri("/hello")
                .headers(headers -> headers.setBasicAuth("user", "user"))
                .exchange()
                .expectStatus().isOk();
    }
}
