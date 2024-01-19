package com.ethan.system.pdf.utils;


import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class PdfUtils {
    private final ReentrantLock lock = new ReentrantLock();
    private static final Map<String, byte[]> fontBinaryMap = new ConcurrentHashMap<>();

    public static List<byte[]> getFontBinary(List<String> fontList) throws IOException {
        Set<String> sets = fontBinaryMap.keySet();
        if (sets.containsAll(fontList)) {
            return fontList.stream().map(fontBinaryMap::get).collect(Collectors.toList());
        } else {
            synchronized (PdfUtils.class) {
                log.info("FontBinary initializing...");
                if (!fontBinaryMap.keySet().containsAll(fontList)) {
                    for (String path : fontList) {
                        log.info("fontPath:{}", path);
                        if (!fontBinaryMap.containsKey(path)) {
                            byte[] byteArray = IOUtils.toByteArray(new ClassPathResource(path).getInputStream());
                            fontBinaryMap.put(path, byteArray);
                        }
                    }
                }
                return fontList.stream().map(fontBinaryMap::get).collect(Collectors.toList());
            }
        }
    }

    /**
     * to solve the concurrency  java.lang.IllegalStateException: Cipher not initialized issue,
     * move the merge with lock if there is encryption.
     *
     * 在并发场景，合并PDF并且添加密码的时候，由于Cipher不是线程安全的，会报异常。因此需要添加锁来处理
     *
     * @param pdfDocuments
     * @param password
     * @param needPwd
     * @return
     */
    public static ByteArrayOutputStream merge(List<PdfDocument> pdfDocuments, String password, boolean needPwd){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer;
        if (!needPwd) {
            writer = new PdfWriter(outputStream);
            writer.setSmartMode(true);
            try (PdfDocument pdfDocument = new PdfDocument(writer)) {
                for (PdfDocument document : pdfDocuments) {
                    document.copyPagesTo(1, document.getNumberOfPages(), pdfDocument);
                    document.close();
                }
            }
            return outputStream;
        }

        try {
            if (lock.tryLock(60, TimeUnit.SECONDS)) {
                writer = new PdfWriter(outputStream,
                        new WriterProperties()
                                .setStandardEncryption(
                                        password.getBytes(StandardCharsets.UTF_8),
                                        password.getBytes(StandardCharsets.UTF_8),
                                        EncryptionConstants.ALLOW_PRINTING,
                                        EncryptionConstants.ENCRYPTION_AES_256
                                )
                );
                // PdfWriter writer = new PdfWriter(Paths.get("/logs/dep", "thymeleaf_test.pdf").toString());
                writer.setSmartMode(true);
                try (PdfDocument pdfDocument = new PdfDocument(writer)) {
                    for (PdfDocument document : pdfDocuments) {
                        document.copyPagesTo(1, document.getNumberOfPages(), pdfDocument);
                        document.close();
                    }
                }
            } else {
                log.error("Failed to acquire lock in thread:{}", Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("Failed to acquire lock in InterruptedException:{}", Thread.currentThread().getName(), e);
            /* Clean up whatever needs to be handled before interrupting  */
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        /** backup option
         try (PdfDocument pdfDocument = new PdfDocument(writer)) {
         PdfMerger merger = new PdfMerger(pdfDocument, false, false);
         for (PdfDocument document : pdfDocuments) {
         merger.merge(document, 1, document.getNumberOfPages());
         document.close();
         }
         } */
        return outputStream;
    }

}
