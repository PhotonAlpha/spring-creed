package com.ethan.server;

import com.ethan.system.dal.repository.permission.MenuRepository;
import com.ethan.system.pdf.PdfApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
@Disabled
public class TemplateEngineTest {

    @Resource(name = "txtSpringTemplateEngine")
    private SpringTemplateEngine txtSpringTemplateEngine;
    @Resource
    private MenuRepository menuRepository;
    @Test
    void generateTemplate() throws IOException {
        var menuList = menuRepository.findAll();

        Context context = new Context();
        context.setVariable("menus", menuList);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        txtSpringTemplateEngine.process("ExportMenu", context, new OutputStreamWriter(output));

        Path rootPath = Paths.get("./");
        Files.write(rootPath.resolve("data.sql"), output.toByteArray());
    }
}
