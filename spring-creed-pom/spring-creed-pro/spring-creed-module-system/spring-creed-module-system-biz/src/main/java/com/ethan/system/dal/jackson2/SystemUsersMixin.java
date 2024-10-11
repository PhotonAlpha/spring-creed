package com.ethan.system.dal.jackson2;

import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.system.dal.entity.permission.SystemUsers;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 9/20/24
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = SystemUsersDeserializer.class)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SystemUsersMixin {
}


class SystemUsersDeserializer extends JsonDeserializer<SystemUsers> {
    @Override
    public SystemUsers deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode root = mapper.readTree(parser);
        return deserialize(parser, mapper, root);
    }

    private SystemUsers deserialize(JsonParser parser, ObjectMapper mapper, JsonNode root)
            throws JsonProcessingException {
        if (!Objects.isNull(root)) {
            return JacksonUtils.parseObject(root.toString(), SystemUsers.class);
        }
        return null;
    }
    abstract class JsonNodeUtils {

        static final TypeReference<Set<String>> STRING_SET = new TypeReference<Set<String>>() {
        };

        static final TypeReference<Map<String, Object>> STRING_OBJECT_MAP = new TypeReference<Map<String, Object>>() {
        };

        static String findStringValue(JsonNode jsonNode, String fieldName) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isTextual()) ? value.asText() : null;
        }

        static <T> T findValue(JsonNode jsonNode, String fieldName, TypeReference<T> valueTypeReference,
                               ObjectMapper mapper) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isContainerNode()) ? mapper.convertValue(value, valueTypeReference) : null;
        }

        static JsonNode findObjectNode(JsonNode jsonNode, String fieldName) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isObject()) ? value : null;
        }

    }
}

