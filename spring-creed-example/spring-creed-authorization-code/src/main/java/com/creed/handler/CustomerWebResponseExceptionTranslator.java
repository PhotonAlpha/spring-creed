package com.creed.handler;

import com.creed.constant.ResponseEnum;
import com.creed.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 自定义异常message， 规范参考 README.md
 */
@Slf4j
public class CustomerWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {
  private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

  @Override
  public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
    // Try to extract a SpringSecurityException from the stacktrace
    Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);
    Exception ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);

    if (ase != null) {
      return handle(ase, ResponseEnum.UNAUTHORIZED);
    }

    ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class,
        causeChain);
    if (ase != null) {
      return handle(ase, ResponseEnum.UNAUTHORIZED);
    }

    ase = (AccessDeniedException) throwableAnalyzer
        .getFirstThrowableOfType(AccessDeniedException.class, causeChain);
    if (ase instanceof AccessDeniedException) {
      return handle(ase, ResponseEnum.INSUFFICIENT_PERMISSIONS);
    }

    ase = (HttpRequestMethodNotSupportedException) throwableAnalyzer.getFirstThrowableOfType(
        HttpRequestMethodNotSupportedException.class, causeChain);
    if (ase instanceof HttpRequestMethodNotSupportedException) {
      return handle(ase, ResponseEnum.ACCESS_TOKEN_INVALID);
      //return handleOAuth2Exception(new MethodNotAllowed(ase.getMessage(), ase));
    }

    return handle(new ServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e), ResponseEnum.INCORRECT_PARAMS);

  }

  public ResponseEntity<OAuth2Exception> handle(Exception e, ResponseEnum responseEnum) throws IOException, ServletException {
    ResponseVO error = ResponseVO.error(responseEnum);
    log.error("Exception Occur: {}",e.getMessage());
    //int status = HttpStatus.UNAUTHORIZED;
    HttpHeaders headers = new HttpHeaders();
    headers.set("Cache-Control", "no-store");
    headers.set("Pragma", "no-cache");
    //if ( status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
    //  headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
    //}
    OAuth2Exception ex = new OAuth2Exception(responseEnum.getMessage());
    ex.addAdditionalInformation("code", ""+responseEnum.getCode());
    ResponseEntity<OAuth2Exception> response = new ResponseEntity<OAuth2Exception>(ex, headers,
        HttpStatus.FORBIDDEN);

    return response;
  }

}
