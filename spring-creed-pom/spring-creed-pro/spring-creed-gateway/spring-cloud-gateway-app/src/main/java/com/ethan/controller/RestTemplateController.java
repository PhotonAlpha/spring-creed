package com.ethan.controller;

import com.ethan.controller.dto.StudentDTO;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 27/3/24
 */
@RestController
public class RestTemplateController {
    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/rest/test")
    private Mono<StudentDTO> getInfo2() {
        StudentDTO st = new StudentDTO();
        st.setUsername("xiaomi");
        st.setGender("male");
        st.setAge(18);

        ResponseEntity<StudentDTO> responseEntity = restTemplate.postForEntity("http://localhost:8088/person/student2", st, StudentDTO.class);
        StudentDTO body = responseEntity.getBody();
        body.setId(UUID.randomUUID().toString());
        return Mono.just(body);
    }
}
