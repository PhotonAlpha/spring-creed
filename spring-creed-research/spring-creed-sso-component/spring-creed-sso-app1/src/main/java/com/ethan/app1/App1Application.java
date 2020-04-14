/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/08
 */
package com.ethan.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class
})
public class App1Application {
  public static void main(String[] args) {
    SpringApplication.run(App1Application.class, args);
  }

  /*@Bean
  OAuth2RestTemplate oAuth2RestTemplate(OAuth2ClientContext clientContext, OAuth2ProtectedResourceDetails details){
    return new OAuth2RestTemplate(details, clientContext);
  }*/
}
