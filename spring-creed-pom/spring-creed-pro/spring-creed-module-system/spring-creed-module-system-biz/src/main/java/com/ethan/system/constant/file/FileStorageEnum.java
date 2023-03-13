package com.ethan.system.constant.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件存储器枚举
 *
 * 
 */
@AllArgsConstructor
@Getter
public enum FileStorageEnum {

    DB(1, String.class, String.class),

    LOCAL(10, String.class, String.class),
    FTP(11, String.class, String.class),
    SFTP(12, String.class, String.class),

    S3(20, String.class, String.class),
    ;

    /**
     * 存储器
     */
    private final Integer storage;

    /**
     * 配置类
     */
    private final Class<? extends Object> configClass;
    // private final Class<? extends FileClientConfig> configClass;
    /**
     * 客户端类
     */
    private final Class<? extends Object> clientClass;
    // private final Class<? extends FileClient> clientClass;

    public static FileStorageEnum getByStorage(Integer storage) {
        return Arrays.stream(values()).filter(o -> o.getStorage().equals(storage)).findFirst().orElse(null);
    }

}
