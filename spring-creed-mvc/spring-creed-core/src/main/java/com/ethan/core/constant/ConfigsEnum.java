package com.ethan.core.constant;

public enum ConfigsEnum {
  /**
   * @Description prefix
   **/
  PREFIX("^^["),
  SUFFIX("]"),
  PRIVATE_KEY("certificate/Cert.jks"),
  PUBLIC_KEY("certificate/Public.csr"),
  KEY_PASS("certificate/keypass.text");

  private String value;

  ConfigsEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
