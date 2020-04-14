/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/13
 */
package com.ethan.app1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

@Configuration
public class TemplateConfig {
  /*@Bean
  public OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails() {
    AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
    return details;
  }*/

  /**
   * 触发重定向 {@link org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter}
   *
   * {@link org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter#successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)}
   * 注入 authentication
   *
   *
   * 然后经过 {@link org.springframework.security.web.authentication.AnonymousAuthenticationFilter}
   * 判断是否有 Authentication
   *
   * @param clientContext
   * @param details
   * @return
   *
   * AuthorizationCodeResourceDetails
   */
  @Bean
  OAuth2RestTemplate oAuth2RestTemplate(OAuth2ClientContext clientContext, OAuth2ProtectedResourceDetails details){
    return new OAuth2RestTemplate(details, clientContext);
  }
}
