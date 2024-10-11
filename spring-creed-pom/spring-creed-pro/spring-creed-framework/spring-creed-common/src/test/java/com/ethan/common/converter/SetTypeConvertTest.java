package com.ethan.common.converter;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 11/10/24
 */
public class SetTypeConvertTest {
    @Test
    void setTypeConvertTest() {
        System.out.println(StringUtils.join(new HashSet<>(), ","));
    }
}
