# SSO 原理

参考: https://segmentfault.com/a/1190000020398550

注意SSO概念

1. client 先 通过SSO clientId 访问
    http://localhost:8081/authserver/oauth/authorize?client_id=oauth2_client&redirect_uri=http://localhost:8080/login&response_type=code&scope=read%20write&state=J2aa6
    http://localhost:8081/authserver/oauth/token?client_id=oauth2_client&client_secret=oauth2_client_secret&code=1j4JeG&redirect_uri=http://localhost:8080/login&grant_type=authorization_code&scope=read write
    获取token

2. client 从springContext获取Authentication,获取token访问Resource Server

3. 当 SESSION 清除之后, client会再次尝试去 EnableAuthorizationServer 获取该client的token,如果未登出,获取的token会是之前的.

4. 如果已登出,则再次跳转到登录页面,重新登录


check_token: http://localhost:8081/authserver/oauth/check_token?token=09721a67-a227-43ce-9c99-799e8a9f7f8f1

EnableOAuth2SSo和EnableOAuth2Client的区别是EnableOAuth2SSo会在过滤器链中添加OAuth2ClientAuthenticationProcessingFilter

接口服务器使用@EnableOAuth2Client,UI 服务器使用EnableOAuth2SSo