package com.ethan.std.deserialize;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import javax.security.auth.Subject;
import java.io.IOException;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/5/2022 4:17 PM
 */
public class OAuth2AuthenticationDeserializer extends JsonDeserializer<OAuth2Authentication> {
    @Override
    public OAuth2Authentication deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        JsonNode requestNode = jsonNode.get("storedRequest");
        JsonNode userAuthenticationNode = jsonNode.get("userAuthentication");

        OAuth2Request oAuth2Request = mapper.readValue(requestNode.toPrettyString(), OAuth2Request.class);
        Authentication auth;
        if (userAuthenticationNode != null && !userAuthenticationNode.isEmpty()) {
            auth = mapper.readValue(requestNode.toPrettyString(), Authentication.class);
        }


        // OAuth2Authentication token = new OAuth2Authentication(oAuth2Request, auth);
        // JsonNode details = jsonNode.get("details");
        //
        // mapper.readValue(details.traverse(mapper), details);
        //
        // val detailsNode = jsonNode.readJsonNode("details")
        // if (detailsNode != null && detailsNode !is MissingNode) {
        //     token.details = mapper.readValue(detailsNode.traverse(mapper), OAuth2AuthenticationDetails::class.java)
        // }
        // return token

        return null;
    }
}
