/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.service;

import com.ethan.vo.FileDetailsVo;
import com.ethan.vo.R;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: vue-console
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/15/2022 12:06 PM
 */
@Service
public class FileUploadServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Value("${file.dir:/logs/static}")
    private String fileDir;

    public R listFiles() {
        try {
            List<FileDetailsVo> pathList = new ArrayList<>();
            try (Stream<Path> stream = Files.walk(Paths.get(fileDir))) {
                pathList = stream.map(Path::normalize)
                        .filter(Files::isRegularFile)
                        .map(p -> new FileDetailsVo(p.toAbsolutePath().toString()))
                        .collect(Collectors.toList());
            }
            return R.success(pathList);
        } catch (IOException e) {
            log.error("get error");
            return R.error(500, e.getMessage());
        }

        // path.
    }

    public R handlerFileUpload(MultipartFile[] files, String pwd ) {
        List<String> errors = new ArrayList<>();
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (MultipartFile file : files) {
            try {
                handlerMultipartFile(file, pwd);
            } catch (Exception e) {
                log.error(file.getOriginalFilename() + " occur error", e);
                errors.add(e.getMessage());
            }
        }
        if (CollectionUtils.isEmpty(errors)) {
            return R.success("upload successful");
        } else {
            return R.error(500, errors);
        }
    }

    private void handlerMultipartFile(MultipartFile file, String password) throws Exception {
        String filename = Optional.ofNullable(file)
                .map(MultipartFile::getOriginalFilename)
                .orElse(StringUtils.EMPTY);
        log.info("uploading filename:{}", filename);

        if (StringUtils.isNotBlank(filename)) {
            if (StringUtils.endsWith(filename, ".7z")) {
                uncompress(file, password);
            } else {
                // just copy
                file.transferTo(Path.of(fileDir + File.separator + file.getOriginalFilename()));
            }
        }
    }

    public static void compress(String name, File... files) throws IOException {
        try (SevenZOutputFile out = new SevenZOutputFile(new File(name))){
            for (File file : files){
                addToArchiveCompression(out, file, ".");
            }
        }
    }
    private static void addToArchiveCompression(SevenZOutputFile out, File file, String dir) throws IOException {
        String name = dir + File.separator + file.getName();
        if (file.isFile()){
            SevenZArchiveEntry entry = out.createArchiveEntry(file, name);
            out.putArchiveEntry(entry);
            // FileInputStream in = new FileInputStream(file);
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] bytesIn = new byte[1024];
                int read = 0;
                while ((read = bis.read(bytesIn)) != -1) {
                    out.write(bytesIn, 0, read);
                }
            } finally {
                out.closeArchiveEntry();
            }
            /* byte[] b = new byte[1024];
            int count = 0;
            while ((count = in.read(b)) > 0) {
                out.write(b, 0, count);
            } */
        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null){
                for (File child : children){
                    addToArchiveCompression(out, child, name);
                }
            }
        } else {
            log.info(file.getName() + " is not supported");
        }
    }

    private void uncompress(MultipartFile file, String password) throws IOException {
        log.info("start unzip");

        String suffixStr = "." + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        String prefixStr = StringUtils.substringBeforeLast(file.getOriginalFilename(), ".");


        Path path = Files.createTempFile(prefixStr, suffixStr);
        File tmpFile = path.toFile();
        FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
        log.info("create tmp file:{}", path.toAbsolutePath());

        try (SevenZFile sevenZFile = new SevenZFile(tmpFile, password.toCharArray())) {
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {
                Path filePath = Paths.get(fileDir, entry.getName());
                log.info("uncompressing file:{}", filePath.toAbsolutePath());
                if (!entry.isDirectory()) {
                    unzipFiles(sevenZFile, filePath);
                } else {
                    Files.createDirectories(filePath);
                }
                entry = sevenZFile.getNextEntry();
            }
        } finally {
            Files.deleteIfExists(path);
        }


        /* ZipFile zipFile = new ZipFile("");
        if (zipFile.isEncrypted()) {

        }
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry = zipInputStream.getNextEntry();

            while (entry != null) {
                Path filePath = Paths.get(fileDir, entry.getName());
                if (!entry.isDirectory()) {
                    unzipFiles(zipInputStream, filePath);
                } else {
                    Files.createDirectories(filePath);
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        } */
    }
    private void unzipFiles(final SevenZFile sevenZFile, final Path unzipFilePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
            byte[] bytesIn = new byte[1024];
            int read = 0;
            while ((read = sevenZFile.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
