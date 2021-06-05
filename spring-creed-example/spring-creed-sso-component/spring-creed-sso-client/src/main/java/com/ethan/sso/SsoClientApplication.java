/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/08
 */
package com.ethan.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsoClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(SsoClientApplication.class, args);
  }

  /*@Bean
  OAuth2RestTemplate oAuth2RestTemplate(OAuth2ClientContext clientContext, OAuth2ProtectedResourceDetails details){
    return new OAuth2RestTemplate(details, clientContext);
  }*/
}
