package com.ethan.validator.context;

import com.ethan.validator.config.JsonSchemaConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

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
    @Resource
    AutowireCapableBeanFactory beanFactory;

    private JsonSchemaValidated jsonSchemaValidated;
    private ObjectMapper mapper;
    private com.networknt.schema.JsonSchema jsonSchema;
    private final List<AbstractBusinessValidator<Object>> constraintList = new CopyOnWriteArrayList<>();

    @Override
    public void initialize(JsonSchemaValidated constraintAnnotation) {
        // log.info("Thead initialize:{} bean:{}", Thread.currentThread().getName(), this);
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.registerModule(new JavaTimeModule());
        this.jsonSchemaValidated = constraintAnnotation;
        this.jsonSchema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://www.example.org/%s.json".formatted(jsonSchemaValidated.schemaUri())), loadSchemaValidatorsConfig());

        Class<? extends AbstractBusinessValidator<?>>[] bizValidators = jsonSchemaValidated.constraints();
        if (ArrayUtils.isNotEmpty(bizValidators)) {
            Stream.of(bizValidators).forEach(validator -> {
                AbstractBusinessValidator<?> businessValidator = beanFactory.createBean(validator);
                constraintList.add((AbstractBusinessValidator<Object>) businessValidator);
            });
        }
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
        String schemaUri = jsonSchemaValidated.schemaUri();
        log.info("Thead Name:{} schemaUri:{} starting check @{}", Thread.currentThread().getName(), schemaUri, this);
        if (StringUtils.isBlank(schemaUri)) {
            return false;
        }
        try {
            String jsonStr = mapper.writeValueAsString(source);
            // if not cacheable, create new always
            if (!jsonSchemaConfiguration.isCache()) {
                this.jsonSchema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://www.example.org/%s.json".formatted(jsonSchemaValidated.schemaUri())), loadSchemaValidatorsConfig());
            }
            Set<ValidationMessage> errors = jsonSchema.validate(jsonStr, InputFormat.JSON, executionContext ->
                    // By default since Draft 2019-09 the format keyword only generates annotations and not assertions
                    executionContext.getExecutionConfig().setFormatAssertionsEnabled(true)
            );
            if (CollectionUtils.isEmpty(errors)) {
                return doBusinessConstraintViolation(source, constraintValidatorContext);
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


    boolean doBusinessConstraintViolation(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (!constraintList.isEmpty()) {
            boolean allMatch = constraintList.stream().allMatch(validator -> {
                log.info("instance:{}", validator.toString());
                // can execute businessValidator initialize if required
                return validator.isValid(value, constraintValidatorContext);
            });
            log.info("validator all match:{}", allMatch);
            return allMatch;
        }
        return true;
    }


}
