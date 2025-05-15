package com.ethan.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.jakarta.JsonSchemaGenerator;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 20/2/25
 */
public class MockJsonSchemaTest implements BaseTestSupport {
    @Test
    void jsonSchemaTest() {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012, builder ->
                // This creates a mapping from $id which starts with https://www.example.org/ to the retrieval URI classpath:schema/
                builder.schemaMappers(schemaMappers -> schemaMappers.mapPrefix("https://www.example.org/", "classpath:schema/"))
        );

        SchemaValidatorsConfig.Builder builder = SchemaValidatorsConfig.builder();
        SchemaValidatorsConfig config = builder.errorMessageKeyword("errorMessage")
                .pathType(PathType.URI_REFERENCE)//设置路径格式，如果是JSON_POINTER, 就会是 /phone
                .build();
        String schemaStr = readFile("schema/my-account-create.json");
        JsonSchema schema = jsonSchemaFactory.getSchema(schemaStr, config);
        schema.getSchemaNode().toString();
    }

    @Test
    void generateSchema() {
        String schemaStr = readFile("schema/my-account-create.json");
        String mockRequestStr = readFile("mock-request.json");

        Set<ValidationMessage> validationMessages = schemaValidate(schemaStr, mockRequestStr);
        System.out.println(validationMessages);
    }
    @Test
    void generateSchemaV201909() {
        String schemaStr = readFile("schema/V201909/my-account-create.json");
        String mockRequestStr = readFile("mock-request-missing.json");

        Set<ValidationMessage> validationMessages = schemaValidate(schemaStr, mockRequestStr);
        System.out.println(validationMessages);
    }

    @Test
    void generateSchema_Date() {
        // JsonSchema schema = schemaGen.generateSchema()
        Instant nowUtc = Instant.now();
        ZoneId asiaThailand = ZoneId.of("UTC+7");
        ZonedDateTime nowThailand = ZonedDateTime.ofInstant(nowUtc, asiaThailand);
        System.out.println("inside getZoneCurrentDateWithTime  nowThailand : " + nowThailand);
        Date date = Date.from(nowThailand.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(date);
        date = Date.from(nowThailand.toLocalDateTime().atZone(ZoneId.of("UTC+0")).toInstant());
        System.out.println(date);
    }

    @Test
    void name() {
        Object o = new Object();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> validate = validator.validate(o);
    }
}
