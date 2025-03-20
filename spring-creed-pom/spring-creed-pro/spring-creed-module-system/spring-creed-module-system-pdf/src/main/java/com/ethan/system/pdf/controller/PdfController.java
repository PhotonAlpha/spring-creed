/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 */

package com.ethan.system.pdf.controller;


import com.ethan.system.pdf.controller.dto.BootstrapPath;
import com.ethan.system.pdf.controller.dto.Company;
import com.ethan.system.pdf.controller.dto.Employee;
import com.ethan.system.pdf.utils.PdfUtils;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Controller
@Slf4j
public class PdfController {
    @Resource
    private TemplateEngine templateEngine;

    @GetMapping("/show/{tmpName}")
    @PermitAll
    public String showHtml(@PathVariable("tmpName") String tmpName, Model mode, HttpServletRequest request) throws IOException {
        String baseUrl = getCurrentBaseUrl();
        log.info("baseUrl:{}", baseUrl);
        mode.addAttribute("baseUrl", baseUrl);

        // Locale locale = new Locale("zh", "TC");
        // 注意区别大小写， 在linux中 messages_zh_CN.properties country 不能是小写，否则无法识别。
        // Locale locale = new Locale("zh", "CN");
        Locale locale = new Locale("en");
        new SessionLocaleResolver().setLocale(request, null, locale);

        if (StringUtils.equalsIgnoreCase("cover_index", tmpName)) {
            mode.addAttribute("fragmentTemplate", "fragments/cover_fragment");
            mode.addAttribute("fragmentName", "cover");
        } else {
            mode.addAttribute("fragmentTemplate", "fragments/main_fragment");
            mode.addAttribute("fragmentName", "main");
        }
        Company company = new Company();
        company.setEmployees(Arrays.asList(
                new Employee("ethan", "neco", "ethan@xxx.com", "86", "1314520xxxx"),
                new Employee("danny", "neo", "danny@xxx.com", "86", "1314510xxxx")
        ));
        mode.addAttribute("company", company);


        // generatePdf(locale);
        return tmpName;
    }

    public static String getCurrentBaseUrl() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }

    @SneakyThrows
    private void generatePdf(Locale locale) {
        Context context = new Context();
        context.setLocale(locale);

        Company company = new Company();
        company.setEmployees(Arrays.asList(
                new Employee("ethan", "neco", "ethan@xxx.com", "86", "1314520xxxx"),
                new Employee("danny", "neo", "danny@xxx.com", "86", "1314510xxxx")
        ));

        context.setVariable("company", company);


        try (ByteArrayOutputStream os1 = generatePdf(context, "index", "fragments/main_fragment", "main", locale);
             ByteArrayOutputStream os2 = generateCoverPage("Cover Header", "cover_index", "fragments/cover_fragment", "cover", locale)) {
            ByteArrayOutputStream os = merge(Arrays.asList(
                    new PdfDocument(
                            new PdfReader(new ByteArrayInputStream(os2.toByteArray()))),
                    new PdfDocument(
                            new PdfReader(new ByteArrayInputStream(os1.toByteArray())))
            ), "pwd", false);
            Files.write(Paths.get("/logs", "thymeleaf_test.pdf"), os.toByteArray(), StandardOpenOption.CREATE);
        }

    }
    protected List<String> fontList(Locale locale) {
        if (StringUtils.equals(locale.getLanguage(), "zh")) {
            return Arrays.asList(
                    "static/fonts/NotoSans/NotoSansSC-Regular.ttf",
                    "static/fonts/NotoSans/NotoSansSC-Bold.ttf",
                    "static/fonts/NotoSans/NotoSansSC-Light.ttf"
            );
        }
        return Arrays.asList(
                "static/fonts/OpenSans/OpenSans-Regular.ttf",
                "static/fonts/OpenSans/OpenSans-Bold.ttf",
                "static/fonts/OpenSans/OpenSans-Light.ttf"
        );
    }


    public ByteArrayOutputStream generatePdf(Context context, String template, String fragmentTemplate, String fragmentName, Locale locale) throws IOException {
        String customCssContentPath;
        if (StringUtils.equals(locale.getLanguage(), "zh")) {
            customCssContentPath = new ClassPathResource("static/css/custom.SC.css").getFile().getAbsolutePath();
        } else {
            customCssContentPath = new ClassPathResource("static/css/custom.css").getFile().getAbsolutePath();
        }
        String cssContentPath = new ClassPathResource("static/css/bootstrap.min.css").getFile().getAbsolutePath();
        String messagePath = new ClassPathResource("static/img/message.svg").getFile().getAbsolutePath();
        String moneyPath = new ClassPathResource("static/img/money.svg").getFile().getAbsolutePath();
        String peoplesPath = new ClassPathResource("static/img/peoples.svg").getFile().getAbsolutePath();
        String profilePath = new ClassPathResource("static/img/avatar.gif").getFile().getAbsolutePath();
        String iconCheckedPath = new ClassPathResource("static/img/IconChecked.png").getFile().getAbsolutePath();
        String iconUncheckedPath = new ClassPathResource("static/img/IconUnchecked.png").getFile().getAbsolutePath();
        String shoppingPath = new ClassPathResource("static/img/shopping.svg").getFile().getAbsolutePath();
        BootstrapPath bootstrapPath = new BootstrapPath();
        bootstrapPath.setCustomCssContent(customCssContentPath);
        bootstrapPath.setCssContent(cssContentPath);
        bootstrapPath.setMessageImg(messagePath);
        bootstrapPath.setMoneyImg(moneyPath);
        bootstrapPath.setPeoplesImg(peoplesPath);
        bootstrapPath.setProfileImg(profilePath);
        bootstrapPath.setShoppingImg(shoppingPath);
        bootstrapPath.setIconCheckedImg(iconCheckedPath);
        bootstrapPath.setIconUncheckedImg(iconUncheckedPath);

        context.setVariable("bootstrapPath", bootstrapPath);
        context.setVariable("fragmentTemplate", fragmentTemplate);
        context.setVariable("fragmentName", fragmentName);

        String htmlContent = templateEngine.process(template, context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document;
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDocument = new PdfDocument(writer)) {

            // pdfDocument.setTagged();
            PageSize pageSize = PageSize.A4.rotate();
            pdfDocument.setDefaultPageSize(pageSize);

            // Configure ConverterProperties to ignore exceptions
            ConverterProperties converterProperties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider(false, false, false);

            for (String fontPath : fontList(locale)) {
                FontProgram fontProgram = FontProgramFactory.createFont(new ClassPathResource(fontPath).getFile().getAbsolutePath());
                log.info("fontPath:{}", fontPath);
                fontProvider.addFont(fontProgram);
            }
            converterProperties.setFontProvider(fontProvider);

            /* document = HtmlConverter.convertToDocument(htmlContent, pdfDocument, converterProperties);
            document.close(); */
            HtmlConverter.convertToPdf(htmlContent, pdfDocument, converterProperties);
        }

        return outputStream;
        // Files.write(Paths.get("/logs/dep", "thymeleaf_test.pdf"), outputStream.toByteArray(), StandardOpenOption.CREATE);
    }

    protected ByteArrayOutputStream generateCoverPage(String title, String template, String fragmentTemplate, String fragmentName, Locale locale) throws IOException {
        Context context = new Context();
        String customCssContentPath = new ClassPathResource("static/css/custom.css").getFile().getAbsolutePath();
        String cssContentPath = new ClassPathResource("static/css/bootstrap.min.css").getFile().getAbsolutePath();
        String messagePath = new ClassPathResource("static/img/message.svg").getFile().getAbsolutePath();
        String moneyPath = new ClassPathResource("static/img/money.svg").getFile().getAbsolutePath();
        String peoplesPath = new ClassPathResource("static/img/peoples.svg").getFile().getAbsolutePath();
        String profilePath = new ClassPathResource("static/img/avatar.gif").getFile().getAbsolutePath();
        String iconCheckedPath = new ClassPathResource("static/img/IconChecked.png").getFile().getAbsolutePath();
        String iconUncheckedPath = new ClassPathResource("static/img/IconUnchecked.png").getFile().getAbsolutePath();
        String shoppingPath = new ClassPathResource("static/img/shopping.svg").getFile().getAbsolutePath();
        BootstrapPath bootstrapPath = new BootstrapPath();
        bootstrapPath.setCustomCssContent(customCssContentPath);
        bootstrapPath.setCssContent(cssContentPath);
        bootstrapPath.setMessageImg(messagePath);
        bootstrapPath.setMoneyImg(moneyPath);
        bootstrapPath.setPeoplesImg(peoplesPath);
        bootstrapPath.setProfileImg(profilePath);
        bootstrapPath.setShoppingImg(shoppingPath);
        bootstrapPath.setIconCheckedImg(iconCheckedPath);
        bootstrapPath.setIconUncheckedImg(iconUncheckedPath);

        context.setVariable("bootstrapPath", bootstrapPath);
        context.setVariable("fragmentTemplate", fragmentTemplate);
        context.setVariable("fragmentName", fragmentName);
        context.setVariable("title", title);

        // 1. create cover pdf
        String coverContent = templateEngine.process(template, context);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document;
        try (PdfDocument tempPdfDocument = new PdfDocument(new PdfWriter(baos))) {
            // tempPdfDocument.setTagged();
            PageSize pageSize = PageSize.A4.rotate();
            tempPdfDocument.setDefaultPageSize(pageSize);
            // Configure ConverterProperties to ignore exceptions
            ConverterProperties converterProperties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider(false, false, false);

            /* String fontFolder = new ClassPathResource("static/fonts/" + fontName).getFile().getAbsolutePath();
            fontProvider.addDirectory(fontFolder); */
            for (String fontPath : fontList(locale)) {
                FontProgram fontProgram = FontProgramFactory.createFont(new ClassPathResource(fontPath).getFile().getAbsolutePath());
                log.info("fontPath:{}", fontPath);
                fontProvider.addFont(fontProgram);
            }
            converterProperties.setFontProvider(fontProvider);

            /* document = HtmlConverter.convertToDocument(coverContent, tempPdfDocument, converterProperties);
            document.close(); */
            HtmlConverter.convertToPdf(coverContent, tempPdfDocument, converterProperties);
        }
        return baos;
    }

    /**
     * https://kb.itextpdf.com/home/it7kb/examples/adding-a-cover-page-to-an-existing-pdf
     * @param pdfDocuments
     * @param password
     * @param needPwd
     */
    protected ByteArrayOutputStream merge(List<PdfDocument> pdfDocuments, String password, boolean needPwd){
        return PdfUtils.merge(pdfDocuments, password, needPwd);
    }

}
