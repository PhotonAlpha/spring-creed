package com.ethan.configuration.security;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 自定义登录失败Handler
 */
@Component
@Slf4j
public class DefaultAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange()
                .getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            // ResultVO<Map<String, Object>> resultVO = ResultVoUtil.error();
            // 账号不存在
            if (exception instanceof UsernameNotFoundException) {
                // resultVO = ResultVoUtil.failed(UserStatusCodeEnum.ACCOUNT_NOT_EXIST);
                // 用户名或密码错误
            } else if (exception instanceof BadCredentialsException) {
                // resultVO = ResultVoUtil.failed(UserStatusCodeEnum.LOGIN_PASSWORD_ERROR);
                // 账号已过期
            } else if (exception instanceof AccountExpiredException) {
                // resultVO = ResultVoUtil.failed(UserStatusCodeEnum.ACCOUNT_EXPIRED);
                // 账号已被锁定
            } else if (exception instanceof LockedException) {
                // resultVO = ResultVoUtil.failed(UserStatusCodeEnum.ACCOUNT_LOCKED);
                // 用户凭证已失效
            } else if (exception instanceof CredentialsExpiredException) {
                // resultVO = ResultVoUtil.failed(UserStatusCodeEnum.ACCOUNT_CREDENTIAL_EXPIRED);
                // 账号已被禁用
            } else if (exception instanceof DisabledException) {
                // resultVO = ResultVoUtil.failed(UserStatusCodeEnum.ACCOUNT_DISABLE);
            }
            log.error("exception:", exception);
            DataBuffer dataBuffer = dataBufferFactory.wrap("error".getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
