/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.controller.admin.captcha.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointVO {
    private String secretKey;
    public int x;
    public int y;

    public PointVO parse(String jsonStr) {
        Map<String, Object> m = new HashMap();
        Arrays.stream(jsonStr.replaceFirst(",\\{", "\\{").replaceFirst("\\{", "").replaceFirst("\\}", "").replaceAll("\"", "").split(",")).forEach((item) -> {
            m.put(item.split(":")[0], item.split(":")[1]);
        });
        this.setX(Double.valueOf("" + m.get("x")).intValue());
        this.setY(Double.valueOf("" + m.get("y")).intValue());
        this.setSecretKey(m.getOrDefault("secretKey", "") + "");
        return this;
    }

}
