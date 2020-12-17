package com.ethan.core.config;

import com.ethan.core.constant.ConfigsEnum;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;

public class CustomerEncryptPropertyDetector implements EncryptablePropertyDetector {
  @Override
  public boolean isEncrypted(String property) {
    if (property != null) {
      return (property.startsWith(ConfigsEnum.PREFIX.getValue()) && property.endsWith(ConfigsEnum.SUFFIX.getValue()));
    }
    return false;
  }

  @Override
  public String unwrapEncryptedValue(String property) {
    return property.substring(ConfigsEnum.PREFIX.getValue().length(), (property.length() - ConfigsEnum.SUFFIX.getValue().length()));
  }
}
