package com.ethan.framework.util;

import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 20/2/25
 */
@ExtendWith(MockitoExtension.class)
public interface BaseTestSupport {
    Logger log = LoggerFactory.getLogger(BaseTestSupport.class);
    default String readFile(String classpath) {
        try (InputStream in = new ClassPathResource(classpath).getInputStream()) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return null;
    }

    default Set<ValidationMessage> schemaValidate(String schemaJson, String input) {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012, builder ->
                // This creates a mapping from $id which starts with https://www.example.org/ to the retrieval URI classpath:schema/
                builder.schemaMappers(schemaMappers -> schemaMappers.mapPrefix("https://www.example.org/", "classpath:schema/"))
        );

        SchemaValidatorsConfig.Builder builder = SchemaValidatorsConfig.builder();
        SchemaValidatorsConfig config = builder.errorMessageKeyword("errorMessage")
                .pathType(PathType.URI_REFERENCE)//设置路径格式，如果是JSON_POINTER, 就会是 /phone
                .build();

        JsonSchema schema = jsonSchemaFactory.getSchema(schemaJson, config);

        return schema.validate(input, InputFormat.JSON, executionContext -> {
            // By default since Draft 2019-09 the format keyword only generates annotations and not assertions
            executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
        });
    }
}
