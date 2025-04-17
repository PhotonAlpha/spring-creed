package com.ethan;

import com.ethan.config.ExportProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {PropertySourceApplication.class})
@TestPropertySource(properties = {"country_code=CN", "spring.profiles.active=sit"})
public class PropertySourceApplicationTest {
    @Autowired
    private ExportProperties exportProperties;

    @Test
    public void testProperties() {
        String path = exportProperties.getReportTemplatePath();
        System.out.println("--->" + path);
    }
}
