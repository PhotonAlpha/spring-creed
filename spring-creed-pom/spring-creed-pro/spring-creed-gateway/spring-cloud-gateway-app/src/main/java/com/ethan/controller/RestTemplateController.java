package com.ethan.controller;

import com.ethan.controller.dto.StudentDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 27/3/24
 */
@RestController
@Slf4j
@Deprecated(forRemoval = true)
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


    @GetMapping("/connection/test")
    private Mono<StudentDTO> connectionTest() {
        StudentDTO st = new StudentDTO();
        st.setUsername("xiaomi");
        st.setGender("male");
        st.setAge(18);

        ResponseEntity<StudentDTO> responseEntity = restTemplate.getForEntity("http://74.125.203.10", StudentDTO.class);
        StudentDTO body = responseEntity.getBody();
        body.setId(UUID.randomUUID().toString());
        return Mono.just(body);
    }


    @GetMapping("/test/student/{id}")
    private Mono<StudentDTO> getStudentInfo(@PathVariable("id") String id) throws InterruptedException {
        log.info("student:{}", id);
        List<StudentDTO> list = Arrays.asList(
                new StudentDTO("1", "xiaomi", "male", 18),
                new StudentDTO("2", "xiaowang", "male", 20),
                new StudentDTO("3", "xiaohao", "male", 40)
        );
        TimeUnit.SECONDS.sleep(20);
        return Mono.justOrEmpty(list.stream().filter(s -> StringUtils.equals(id, s.getId())).findFirst());
    }
}
