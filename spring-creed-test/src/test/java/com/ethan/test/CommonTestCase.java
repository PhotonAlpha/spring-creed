package com.ethan.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;

public class CommonTestCase {
  @Test
  void testJackson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Student student = new Student("xiao");
    String res = mapper.writeValueAsString(student);
    System.out.println(res);
  }

  @Getter
  static class Student {
    private String name;
    private final String lastName = "ming";

    @JsonCreator
    public Student(String name) {
      this.name = name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @Test
  void testUUID() throws IOException {
    InetAddress inetAddr = InetAddress.getByName("2002:97b:e7aa::97b:e7aa");
    String ipAddr = inetAddr.getHostAddress();
    System.out.println(ipAddr);
    System.out.println(CommonTestCase.getLocalIPv6Address());
  }

  public static String getLocalIPv6Address() throws IOException {
    InetAddress inetAddress = null;
    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
        .getNetworkInterfaces();
    outer:
    while (networkInterfaces.hasMoreElements()) {
      Enumeration<InetAddress> inetAds = networkInterfaces.nextElement()
          .getInetAddresses();
      while (inetAds.hasMoreElements()) {
        inetAddress = inetAds.nextElement();
        //Check if it's ipv6 address and reserved address
        if (inetAddress instanceof Inet6Address
            && !isReservedAddr(inetAddress)) {
          break outer;
        }
      }
    }

    String ipAddr = inetAddress.getHostAddress();
    // Filter network card No
    int index = ipAddr.indexOf('%');
    if (index > 0) {
      ipAddr = ipAddr.substring(0, index);
    }

    return ipAddr;
  }

  /**
   * Check if it's "local address" or "link local address" or
   * "loopbackaddress"
   *
   * @param ip address
   *
   * @return result
   */
  private static boolean isReservedAddr(InetAddress inetAddr) {
    if (inetAddr.isAnyLocalAddress() || inetAddr.isLinkLocalAddress()
        || inetAddr.isLoopbackAddress()) {
      return true;
    }

    return false;
  }
}
