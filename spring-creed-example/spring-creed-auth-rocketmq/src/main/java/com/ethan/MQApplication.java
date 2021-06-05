package com.ethan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.swing.*;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class
})
public class MQApplication {
  public static void main(String[] args) {
    SpringApplication.run(MQApplication.class, args);
    //to fix unknow issue
    System.setProperty("java.awt.headless", "false");
    SwingUtilities.invokeLater(() -> {
      JFrame f = new JFrame("myframe");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setVisible(true);
    });
  }
}

