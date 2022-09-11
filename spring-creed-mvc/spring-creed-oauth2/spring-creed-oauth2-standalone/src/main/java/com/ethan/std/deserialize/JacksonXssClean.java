package com.ethan.std.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/15/2022 5:35 PM
 */
@JsonComponent
public class JacksonXssClean extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null) {
            return null;
        } else {
            return StringEscapeUtils.escapeHtml4(text);
        }
    }
}
