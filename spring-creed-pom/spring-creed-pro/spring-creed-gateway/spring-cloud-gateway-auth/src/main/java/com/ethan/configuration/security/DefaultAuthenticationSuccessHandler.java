package com.ethan.configuration.security;

import com.ethan.utils.JwtTokenUtil;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    /**
     * token 过期时间
     */
    @Value("${jwt.token.expired:30}")
    private int jwtTokenExpired;

    /**
     * 刷新token 时间
     */
    @Value("${jwt.token.refresh.expired:300}")
    private int jwtTokenRefreshExpired;
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            // 生成JWT token
            Map<String, Object> map = new HashMap<>(2);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // map.put("userId", userDetails.getUserId());
            map.put("username", userDetails.getUsername());
            map.put("roles",userDetails.getAuthorities());
            String token = JwtTokenUtil.generateToken(map, userDetails.getUsername(), jwtTokenExpired);
            String refreshToken = JwtTokenUtil.generateToken(map, userDetails.getUsername(), jwtTokenRefreshExpired);
            Map<String, Object> tokenMap = new HashMap<>(2);
            tokenMap.put("token", token);
            tokenMap.put("refreshToken", refreshToken);
            DataBuffer dataBuffer = dataBufferFactory.wrap(JSONObject.toJSONString(tokenMap).getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
