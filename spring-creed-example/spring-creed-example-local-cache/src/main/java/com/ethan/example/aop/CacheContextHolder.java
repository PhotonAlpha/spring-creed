package com.ethan.example.aop;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class CacheContextHolder {

    private static final ThreadLocal<Map<Object, Boolean>> DATA_PERMISSIONS =
            ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, String>> TRACE_ID_MAPPING =
            ThreadLocal.withInitial(HashMap::new);

    public static Boolean get(Object key) {
        return DATA_PERMISSIONS.get().getOrDefault(key, true);
    }
    public static String getTraceId(String correlateId) {
        return TRACE_ID_MAPPING.get().entrySet().stream().filter(entry -> StringUtils.equals(entry.getValue(), correlateId))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    public static String getCorrelateId(String traceId) {
        return TRACE_ID_MAPPING.get().getOrDefault(traceId, null);
    }
    public static void setTraceId(String traceId, String correlateId) {
        TRACE_ID_MAPPING.get().put(traceId, correlateId);
    }
    public static void removeTraceId(String key) {
        TRACE_ID_MAPPING.get().remove(key);
    }

    public static Map<Object, Boolean> getAll() {
        return DATA_PERMISSIONS.get();
    }
    public static Map<String, String> getAllTraces() {
        return TRACE_ID_MAPPING.get();
    }

    public static void add(Object key, Boolean hint) {
        DATA_PERMISSIONS.get().put(key, hint);
    }

    /**
     * 出栈 DataPermission 注解
     *
     * @return DataPermission 注解
     */
    public static Boolean remove(Object key) {
        return DATA_PERMISSIONS.get().remove(key);
    }

    public static void clear() {
        DATA_PERMISSIONS.remove();
        TRACE_ID_MAPPING.remove();
    }

}
