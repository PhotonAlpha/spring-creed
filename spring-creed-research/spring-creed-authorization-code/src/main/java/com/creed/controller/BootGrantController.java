package com.creed.controller;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * {@link org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint}
 */
@Controller
 //必须配置
@SessionAttributes("authorizationRequest")
public class BootGrantController {
  private final CsrfTokenRepository tokenRepository;

  public BootGrantController(CsrfTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @RequestMapping("/oauth/confirm_access")
  public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
    CsrfToken tokenRepo = tokenRepository.loadToken(request);
    String token = Optional.ofNullable(tokenRepo)
        .map(CsrfToken::getToken).orElse("");
    AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
    Set<String> scopes = authorizationRequest.getScope();
    ModelAndView view = new ModelAndView();
    view.setViewName("base-grant");
    view.addObject("clientId", authorizationRequest.getClientId());
    view.addObject("scopes", scopes);
    view.addObject("_csrf", token);
    return view;
  }
}
