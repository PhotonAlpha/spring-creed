package auth.constants;

public enum UrlEnum {
  //oauth2登录
  LOGIN_URL("/oauth/token"),

  ;

  private String url;

  UrlEnum(String url) {
    this.url = url;

  }


  public String getUrl() {
    return url;
  }
}
