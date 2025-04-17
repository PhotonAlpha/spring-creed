package com.ethan.validator.context;

import com.ethan.validator.config.JsonSchemaConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@Slf4j
public class JsonSchemaValidator implements ConstraintValidator<JsonSchemaValidated, Object> {

    @Resource
    JsonSchemaFactory jsonSchemaFactory;
    @Resource
    JsonSchemaConfiguration jsonSchemaConfiguration;

    private String schemaUri;
    private ObjectMapper mapper;
    private JsonSchema jsonSchema;

    @Override
    public void initialize(JsonSchemaValidated constraintAnnotation) {
        // log.info("Thead initialize:{} bean:{}", Thread.currentThread().getName(), this);
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.registerModule(new JavaTimeModule());
        this.schemaUri = constraintAnnotation.schemaUri();
        this.jsonSchema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://www.example.org/%s.json".formatted(this.schemaUri)), loadSchemaValidatorsConfig());
    }

    private SchemaValidatorsConfig loadSchemaValidatorsConfig() {
        SchemaValidatorsConfig.Builder builder = SchemaValidatorsConfig.builder();
        // By default the JDK regular expression implementation which is not ECMA 262 compliant is used
        // Note that setting this requires including optional dependencies
        // builder.regularExpressionFactory(GraalJSRegularExpressionFactory.getInstance());
        // builder.regularExpressionFactory(JoniRegularExpressionFactory.getInstance());
        return builder
                .errorMessageKeyword("errorMessage")
                // .pathType(PathType.URI_REFERENCE)//设置路径格式，如果是JSON_POINTER, 就会是 /phone
                .pathType(PathType.JSON_PATH)
                .cacheRefs(jsonSchemaConfiguration.isCache()).build();
    }

    @Override
    public boolean isValid(Object source, ConstraintValidatorContext constraintValidatorContext) {
        log.info("Thead Name:{} schemaUri:{} starting check @{}", Thread.currentThread().getName(), schemaUri, this);
        if (StringUtils.isBlank(schemaUri)) {
            return false;
        }
        try {
            String jsonStr = mapper.writeValueAsString(source);
            // if not cacheable, create new always
            if (!jsonSchemaConfiguration.isCache()) {
                this.jsonSchema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://www.example.org/%s.json".formatted(this.schemaUri)), loadSchemaValidatorsConfig());
            }
            Set<ValidationMessage> errors = jsonSchema.validate(jsonStr, InputFormat.JSON, executionContext -> {
                // By default since Draft 2019-09 the format keyword only generates annotations and not assertions
                executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
            });
            if (CollectionUtils.isEmpty(errors)) {
                return true;
            } else {
                HibernateConstraintValidatorContext validatorContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
                validatorContext.disableDefaultConstraintViolation();
                for (ValidationMessage vm : errors) {
                    validatorContext.buildConstraintViolationWithTemplate(vm.getMessage()).addConstraintViolation();
                }
            }
        } catch (JsonProcessingException e) {
            HibernateConstraintValidatorContext validatorContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
            validatorContext.disableDefaultConstraintViolation();
            validatorContext.buildConstraintViolationWithTemplate(ExceptionUtils.getMessage(e)).addConstraintViolation();
        }

        return false;
    }


}
