package com.ethan.common.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/2/24
 */
@Slf4j
public class JpaInterceptor implements StatementInspector {
    private static final String TABLE_NAME_FINDER = "([a-z]+[\\d]+(_)(\\.)[a-z]+)";

    private static final String AS_FINDER = "((as)(\\s)[a-z]+([\\d|a-z]+(_)+)+)";

    private static final String SQL_START = "select";

    private static final Boolean SIMPLE = false;

    private static final Integer SUB_TABLE_START = 0;

    private static final Integer SUB_TABLE_END = 2;

    @Override
    public String inspect(String sql) {
        String lowerSql = sql.toLowerCase().replace("\n", "");
        if (!lowerSql.startsWith(SQL_START)) {
            return sql;
        }
        Pattern table = Pattern.compile(TABLE_NAME_FINDER);
        Matcher tableMatcher = table.matcher(lowerSql);
        List<String> alianTableNames = new ArrayList<>();
        while (tableMatcher.find()) {
            String s = tableMatcher.group();
            String[] split = s.split("\\.");
            alianTableNames.add(split[0]);
        }
        if (SIMPLE) {
            verySimpleSql(lowerSql, alianTableNames);
        } else {
            simpleSql(lowerSql, alianTableNames);
        }
        return sql;
    }
    /**
     * 保留别名的SQL，可以运行
     */
    private void simpleSql(String lowerSql, List<String> alianTableNames) {
        for (String alianTableName : alianTableNames) {
            lowerSql = lowerSql.replace(alianTableName, alianTableName.substring(SUB_TABLE_START, SUB_TABLE_END));
        }
        Pattern as = Pattern.compile(AS_FINDER);
        Matcher asMatcher = as.matcher(lowerSql);
        while (asMatcher.find()) {
            String group = asMatcher.group();
            lowerSql = lowerSql.replace(group, "");
        }
        log.info("JpaInterceptor_simpleSql_lowerSql:{}", lowerSql);
    }

    /**
     * 最简化sql,可能不能直接运行
     */
    private void verySimpleSql(String lowerSql, List<String> alianTableNames) {
        for (String alianTableName : alianTableNames) {
            lowerSql = lowerSql.replace(alianTableName, "");
        }
        lowerSql = lowerSql.replace(".", "");
        Pattern as = Pattern.compile(AS_FINDER);
        Matcher asMatcher = as.matcher(lowerSql);
        while (asMatcher.find()) {
            String group = asMatcher.group();
            lowerSql = lowerSql.replace(group, "");
        }
        log.info("JpaInterceptor_inspect_lowerSql:{}", lowerSql);
    }

}
