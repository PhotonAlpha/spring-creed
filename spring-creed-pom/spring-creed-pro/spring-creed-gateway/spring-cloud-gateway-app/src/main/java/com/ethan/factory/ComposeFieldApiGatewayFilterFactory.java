package com.ethan.factory;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Arrays;
import java.util.List;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 2/4/24
 */
// @Component
public class ComposeFieldApiGatewayFilterFactory extends AbstractGatewayFilterFactory<ComposeFieldApiGatewayFilterFactory.Config> {
    public ComposeFieldApiGatewayFilterFactory(Class<Config> configClass) {
        super(configClass);
    }

    @Resource
    ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilter;

    ParameterizedTypeReference<JsonNode> jsonType = new ParameterizedTypeReference<>() {};

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String currentHost = request.getURI().toString().replaceAll(request.getPath().value(), "");

            // WebClient.create(currentHost)
            //         .get()
            //         // .post()
            //         // .body(BodyInserters.fromValue())
            //         .uri("/spring-cloud-gateway-actuator/showAll");

            return null;
        };

        /* return modifyResponseBodyFilter.apply(c -> {
            c.setRewriteFunction(List.class, JsonNode.class, (filterExchange, input) -> {
                // List<Map<String, Object>> castedInput = input;
                //  extract base field values (usually ids) and join them in a "," separated string

                List<Map<String, Object>> castedInput = new ArrayList<>();
                String baseFieldValues = castedInput.stream()
                        .map(bodyMap -> (String) bodyMap.get(config.getOriginBaseField()))
                        .collect(Collectors.joining(","));


                ServerHttpRequest request = filterExchange.getRequest();
                String currentHost = request.getURI().toString().replaceAll(request.getPath().value(), "");

                // Request to a path managed by the Gateway
                WebClient client = WebClient.create();
                Mono<JsonNode> a = client.get()
                        .uri(UriComponentsBuilder.fromUriString(currentHost)
                                .path(config.getTargetGatewayPath())
                                .queryParam(config.getTargetQueryParam(), baseFieldValues).build().toUri())
                        .exchangeToMono(response -> response.bodyToMono(jsonType)
                                .map(targetEntries -> {
                                    // create a Map using the base field values as keys fo easy access
                                    // Map<String, Map> targetEntriesMap = targetEntries.stream().collect(
                                    //         Collectors.toMap(pr -> (String) pr.get("id"), pr -> pr));
                                    // compose the origin body using the requested target entries
                                    // return castedInput.stream().map(originEntries -> {
                                    //     originEntries.put(config.getComposeField(),
                                    //             targetEntriesMap.get(originEntries.get(config.getOriginBaseField())));
                                    //     return originEntries;
                                    // }).collect(Collectors.toList());
                                    return targetEntries;
                                })
                        );
                return a;
            });
        }); */
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("originBaseField", "targetGatewayPath", "targetQueryParam",
                "composeField");
    }

    public static class Config {
        private String originBaseField;
        private String targetGatewayPath;
        private String targetQueryParam;
        private String composeField;

        public Config() {
        }

        public String getOriginBaseField() {
            return originBaseField;
        }

        public void setOriginBaseField(String originBaseField) {
            this.originBaseField = originBaseField;
        }

        public String getTargetGatewayPath() {
            return targetGatewayPath;
        }

        public void setTargetGatewayPath(String targetGatewayPath) {
            this.targetGatewayPath = targetGatewayPath;
        }

        public String getTargetQueryParam() {
            return targetQueryParam;
        }

        public void setTargetQueryParam(String targetQueryParam) {
            this.targetQueryParam = targetQueryParam;
        }

        public String getComposeField() {
            return composeField;
        }

        public void setComposeField(String composeField) {
            this.composeField = composeField;
        }
    }
}
