package com.ethan.monitor.config;

import com.mzt.logapi.starter.annotation.EnableLogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 7/19/24
 */
@EnableLogRecord(tenant = "") // 貌似用不上 tenant 这玩意给个空好啦
@Configuration
@Slf4j
public class OperateLogConfiguration {
}
