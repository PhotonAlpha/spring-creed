/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.leecode.chapter01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 3/9/2023 4:54 PM
 */
public class GenerateFolder {
    public static void main(String[] args) throws IOException {
        for (int i = 7; i < 100; i++) {
            Files.createDirectory(Path.of("C:\\workspace\\PWEB\\source\\spring-creed\\java-algorithm\\src\\main\\java\\com\\ethan\\leecode").resolve("chapter" + String.format("%02d", i)));
        }
    }
}
