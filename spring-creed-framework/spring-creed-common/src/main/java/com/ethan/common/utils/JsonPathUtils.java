package com.ethan.common.utils;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 13/1/25
 */
@UtilityClass
@Slf4j
public class JsonPathUtils {
    static  {
        Configuration.Defaults configuration = new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return Set.of(Option.SUPPRESS_EXCEPTIONS);
            }
        };
        Configuration.setDefaults(configuration);
    }

    /**
     *  example see {@link JsonPathUtilsTest#readAllTest()}
     * @param jsonStr
     * @param rule  $.store.book[*].price  or $..store.book.price will return List
     * @param clazz convert to List of class type
     * @return  List<T>
     * @param <T>
     */
    public <T> List<T> readAll(String jsonStr, String rule, Class<T> clazz) {
        log.debug("{}",clazz);
        if (StringUtils.isBlank(jsonStr)) {
            jsonStr = "{}";
        }
        var typeRef =  new TypeRef<List<T>>() {
            @Override
            public Type getType() {
                return TypeFactory.defaultInstance().constructCollectionType(List.class, clazz);
            }
        };
        return JsonPath.parse(jsonStr).read(rule, typeRef);
    }
    /**
     *  example see {@link JsonPathUtilsTest#readFirstTest_default()}
     * @param jsonStr
     * @param rule  $.store.book[*].price  or $..store.book.price will return List of first element
     * @param clazz convert to List of class type
     * @param defaultValue if empty return defaultValue value
     * @return  List<T>
     * @param <T>
     */
    public <T> T readFirst(String jsonStr, String rule, Class<T> clazz, T defaultValue) {
        List<T> list = readAll(jsonStr, rule, clazz);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream().findFirst().orElse(defaultValue);
    }

    /**
     * example see {@link JsonPathUtilsTest#readFirstTest()}
     * @param jsonStr
     * @param rule $.store.book[*].price  or $..store.book.price will return List of first element
     * @param clazz convert to List of class type
     * @return
     * @param <T>
     */
    public <T> T readFirst(String jsonStr, String rule, Class<T> clazz) {
        return readFirst(jsonStr, rule, clazz, null);
    }
    public String readFirst(String jsonStr, String rule) {
        return readFirst(jsonStr, rule, String.class);
    }
    public String readFirst(String jsonStr, String rule, String defaultValue) {
        return readFirst(jsonStr, rule, String.class, defaultValue);
    }

    /**
     *  example see {@link JsonPathUtilsTest#readTest()}
     * @param jsonStr
     * @param rule $.store.book[0].price
     * @param clazz return class type
     * @return return value to class type
     * @param <T>
     */
    public <T> T read(String jsonStr, String rule, Class<T> clazz) {
        log.debug("{}",clazz);
        if (StringUtils.isBlank(jsonStr)) {
            jsonStr = "{}";
        }
        return JsonPath.parse(jsonStr).read(rule, clazz);
    }

    /**
     * example see {@link JsonPathUtilsTest#readTest_default()}
     * @param jsonStr
     * @param rule $.store.book[0].price
     * @param clazz return class type
     * @param defaultValue default value if is null
     * @return return value to class type
     * @param <T>
     */
    public <T> T read(String jsonStr, String rule, Class<T> clazz, T defaultValue) {
        log.debug("{}",clazz);
        return ObjectUtils.defaultIfNull(read(jsonStr, rule, clazz), defaultValue);
    }

    /**
     * for string only
     * example see {@link JsonPathUtilsTest#readTest_default()}
     *
     * @param jsonStr
     * @param rule $.store.book[0].price
     * @param defaultValue default string value
     * @return convert value to string
     */
    public String read(String jsonStr, String rule, String defaultValue) {
        return StringUtils.defaultString(read(jsonStr, rule, String.class), defaultValue);
    }

}
