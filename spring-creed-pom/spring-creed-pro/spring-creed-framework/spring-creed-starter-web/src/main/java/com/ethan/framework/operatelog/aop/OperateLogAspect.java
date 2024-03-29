package com.ethan.framework.operatelog.aop;

import com.ethan.common.common.R;
import com.ethan.common.exception.enums.ResponseCodeEnum;
import com.ethan.common.utils.WebFrameworkUtils;
import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.common.utils.monitor.TracerUtils;
import com.ethan.common.utils.servlet.ServletUtils;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.framework.operatelog.constant.OperateTypeEnum;
import com.ethan.framework.operatelog.service.OperateLogFrameworkService;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * 拦截使用 @OperateLog 注解，如果满足条件，则生成操作日志。
 * 满足如下任一条件，则会进行记录：
 * 1. 使用 @Schema + 非 @GetMapping
 * 2. 使用 @OperateLog 注解
 * <p>
 * 但是，如果声明 @OperateLog 注解时，将 enable 属性设置为 false 时，强制不记录。
 *
 */
@Aspect
@Slf4j
public class OperateLogAspect {

    /**
     * 用于记录操作内容的上下文
     *
     * @see com.ethan.framework.operatelog.service.OperateLog#getContent()
     */
    private static final ThreadLocal<String> CONTENT = new ThreadLocal<>();
    /**
     * 用于记录拓展字段的上下文
     *
     * @see com.ethan.framework.operatelog.service.OperateLog#getExts()
     */
    private static final ThreadLocal<Map<String, Object>> EXTS = new ThreadLocal<>();

    @Resource
    private OperateLogFrameworkService operateLogFrameworkService;

    @Around("@annotation(schema)")
    public Object around(ProceedingJoinPoint joinPoint, Schema schema) throws Throwable {
        // 可能也添加了 @Schema 注解
        OperateLog operateLog = getMethodAnnotation(joinPoint,
                OperateLog.class);
        return around0(joinPoint, operateLog, schema);
    }

    @Around("!@annotation(io.swagger.v3.oas.annotations.media.Schema) && @annotation(operateLog)")
    // 兼容处理，只添加 @OperateLog 注解的情况
    public Object around(ProceedingJoinPoint joinPoint,
                         OperateLog operateLog) throws Throwable {
        return around0(joinPoint, operateLog, null);
    }

    private Object around0(ProceedingJoinPoint joinPoint,
                           OperateLog operateLog,
                           Schema schema) throws Throwable {
        // 目前，只有管理员，才记录操作日志！所以非管理员，直接调用，不进行记录
        // Integer userType = WebFrameworkUtils.getLoginUserType();
        // if (!Objects.equals(userType, UserTypeEnum.ADMIN.getValue())) {
        //     return joinPoint.proceed();
        // }

        // 记录开始时间
        Date startTime = new Date();
        try {
            // 执行原有方法
            Object result = joinPoint.proceed();
            // 记录正常执行时的操作日志
            this.log(joinPoint, operateLog, schema, startTime, result, null);
            return result;
        } catch (Throwable exception) {
            this.log(joinPoint, operateLog, schema, startTime, null, exception);
            throw exception;
        } finally {
            clearThreadLocal();
        }
    }

    public static void setContent(String content) {
        CONTENT.set(content);
    }

    public static void addExt(String key, Object value) {
        if (EXTS.get() == null) {
            EXTS.set(new HashMap<>());
        }
        EXTS.get().put(key, value);
    }

    private static void clearThreadLocal() {
        CONTENT.remove();
        EXTS.remove();
    }

    private void log(ProceedingJoinPoint joinPoint,
                     OperateLog operateLog,
                     Schema schema,
                     Date startTime, Object result, Throwable exception) {
        try {
            // 判断不记录的情况
            if (!isLogEnable(joinPoint, operateLog)) {
                return;
            }
            // 真正记录操作日志
            this.log0(joinPoint, operateLog, schema, startTime, result, exception);
        } catch (Throwable ex) {
            log.error("[log][记录操作日志时，发生异常，其中参数是 joinPoint({}) operateLog({}) apiOperation({}) result({}) exception({}) ]",
                    joinPoint, operateLog, schema, result, exception, ex);
        }
    }

    private void log0(ProceedingJoinPoint joinPoint,
                      OperateLog operateLog,
                      Schema schema,
                      Date startTime, Object result, Throwable exception) {
        com.ethan.framework.operatelog.service.OperateLog operateLogObj = new com.ethan.framework.operatelog.service.OperateLog();
        // 补全通用字段
        operateLogObj.setTraceId(TracerUtils.getTraceId());
        operateLogObj.setStartTime(startTime);
        // 补充用户信息
        fillUserFields(operateLogObj);
        // 补全模块信息
        fillModuleFields(operateLogObj, joinPoint, operateLog, schema);
        // 补全请求信息
        fillRequestFields(operateLogObj);
        // 补全方法信息
        fillMethodFields(operateLogObj, joinPoint, operateLog, startTime, result, exception);

        // 异步记录日志
        operateLogFrameworkService.createOperateLog(operateLogObj);
    }

    private static void fillUserFields(com.ethan.framework.operatelog.service.OperateLog operateLogObj) {
        operateLogObj.setUserId(WebFrameworkUtils.getLoginUserId());
        operateLogObj.setUserType(WebFrameworkUtils.getLoginUserType());
    }

    private static void fillModuleFields(com.ethan.framework.operatelog.service.OperateLog operateLogObj,
                                         ProceedingJoinPoint joinPoint,
                                         OperateLog operateLog,
                                         Schema schema) {
        // module 属性
        if (operateLog != null) {
            operateLogObj.setModule(operateLog.module());
        }
        if (StringUtils.isBlank(operateLogObj.getModule())) {
            Tag tag = getClassAnnotation(joinPoint, Tag.class);
            if (tag != null) {
                // 优先读取 @API 的 name 属性
                if (StringUtils.isNotBlank(tag.name())) {
                    operateLogObj.setModule(tag.name());
                }
                // 没有的话，读取 @API 的 tags 属性
                if (StringUtils.isNotBlank(operateLogObj.getModule()) && StringUtils.isNotBlank(tag.description())) {
                    operateLogObj.setModule(tag.description());
                }
            }
        }
        // name 属性
        if (operateLog != null) {
            operateLogObj.setName(operateLog.name());
        }
        if (StringUtils.isNotBlank(operateLogObj.getName()) && schema != null) {
            operateLogObj.setName(schema.name());
        }
        // type 属性
        if (operateLog != null && ArrayUtils.isNotEmpty(operateLog.type())) {
            operateLogObj.setType(operateLog.type()[0].getType());
        }
        if (operateLogObj.getType() == null) {
            RequestMethod requestMethod = obtainFirstMatchRequestMethod(obtainRequestMethod(joinPoint));
            OperateTypeEnum operateLogType = convertOperateLogType(requestMethod);
            operateLogObj.setType(operateLogType != null ? operateLogType.getType() : null);
        }
        // content 和 exts 属性
        operateLogObj.setContent(CONTENT.get());
        operateLogObj.setExts(EXTS.get());
    }

    private static void fillRequestFields(com.ethan.framework.operatelog.service.OperateLog operateLogObj) {
        // 获得 Request 对象
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        // 补全请求信息
        operateLogObj.setRequestMethod(request.getMethod());
        operateLogObj.setRequestUrl(request.getRequestURI());
        operateLogObj.setUserIp(ServletUtils.getClientIP(request));
        operateLogObj.setUserAgent(ServletUtils.getUserAgent(request));
    }

    private static void fillMethodFields(com.ethan.framework.operatelog.service.OperateLog operateLogObj,
                                         ProceedingJoinPoint joinPoint,
                                         OperateLog operateLog,
                                         Date startTime, Object result, Throwable exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        operateLogObj.setJavaMethod(methodSignature.toString());
        if (operateLog == null || operateLog.logArgs()) {
            operateLogObj.setJavaMethodArgs(obtainMethodArgs(joinPoint));
        }
        if (operateLog == null || operateLog.logResultData()) {
            operateLogObj.setResultData(obtainResultData(result));
        }
        operateLogObj.setDuration((int) (System.currentTimeMillis() - startTime.getTime()));
        // （正常）处理 resultCode 和 resultMsg 字段
        if (result instanceof R) {
            R<?> commonResult = (R<?>) result;
            operateLogObj.setResultCode(commonResult.getCode());
            operateLogObj.setResultMsg(commonResult.getMsg());
        } else {
            operateLogObj.setResultCode(ResponseCodeEnum.SUCCESS.getCode());
        }
        // （异常）处理 resultCode 和 resultMsg 字段
        if (exception != null) {
            operateLogObj.setResultCode(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode());
            operateLogObj.setResultMsg(ExceptionUtils.getRootCauseMessage(exception));
        }
    }

    private static boolean isLogEnable(ProceedingJoinPoint joinPoint,
                                       OperateLog operateLog) {
        // 有 @OperateLog 注解的情况下
        if (operateLog != null) {
            return operateLog.enable();
        }
        // 没有 @Schema 注解的情况下，只记录 POST、PUT、DELETE 的情况
        return obtainFirstLogRequestMethod(obtainRequestMethod(joinPoint)) != null;
    }

    private static RequestMethod obtainFirstLogRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtils.isEmpty(requestMethods)) {
            return null;
        }
        return Arrays.stream(requestMethods).filter(requestMethod ->
                requestMethod == RequestMethod.POST
                        || requestMethod == RequestMethod.PUT
                        || requestMethod == RequestMethod.DELETE)
                .findFirst().orElse(null);
    }

    private static RequestMethod obtainFirstMatchRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtils.isEmpty(requestMethods)) {
            return null;
        }
        // 优先，匹配最优的 POST、PUT、DELETE
        RequestMethod result = obtainFirstLogRequestMethod(requestMethods);
        if (result != null) {
            return result;
        }
        // 然后，匹配次优的 GET
        result = Arrays.stream(requestMethods).filter(requestMethod -> requestMethod == RequestMethod.GET)
                .findFirst().orElse(null);
        if (result != null) {
            return result;
        }
        // 兜底，获得第一个
        return requestMethods[0];
    }

    private static OperateTypeEnum convertOperateLogType(RequestMethod requestMethod) {
        if (requestMethod == null) {
            return null;
        }
        switch (requestMethod) {
            case GET:
                return OperateTypeEnum.GET;
            case POST:
                return OperateTypeEnum.CREATE;
            case PUT:
                return OperateTypeEnum.UPDATE;
            case DELETE:
                return OperateTypeEnum.DELETE;
            default:
                return OperateTypeEnum.OTHER;
        }
    }

    private static RequestMethod[] obtainRequestMethod(ProceedingJoinPoint joinPoint) {
        RequestMapping requestMapping = AnnotationUtils.getAnnotation( // 使用 Spring 的工具类，可以处理 @RequestMapping 别名注解
                ((MethodSignature) joinPoint.getSignature()).getMethod(), RequestMapping.class);
        return requestMapping != null ? requestMapping.method() : new RequestMethod[]{};
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(annotationClass);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getClassAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getAnnotation(annotationClass);
    }

    private static String obtainMethodArgs(ProceedingJoinPoint joinPoint) {
        // TODO 提升：参数脱敏和忽略
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        // 拼接参数
        Map<String, Object> args = Maps.newHashMapWithExpectedSize(argValues.length);
        for (int i = 0; i < argNames.length; i++) {
            String argName = argNames[i];
            Object argValue = argValues[i];
            // 被忽略时，标记为 ignore 字符串，避免和 null 混在一起
            args.put(argName, !isIgnoreArgs(argValue) ? argValue : "[ignore]");
        }
        return JacksonUtils.toJsonString(args);
    }

    private static String obtainResultData(Object result) {
        // TODO 提升：结果脱敏和忽略
        if (result instanceof R) {
            result = ((R<?>) result).getData();
        }
        return JacksonUtils.toJsonString(result);
    }

    private static boolean isIgnoreArgs(Object object) {
        Class<?> clazz = object.getClass();
        // 处理数组的情况
        if (clazz.isArray()) {
            return IntStream.range(0, Array.getLength(object))
                    .anyMatch(index -> isIgnoreArgs(Array.get(object, index)));
        }
        // 递归，处理数组、Collection、Map 的情况
        if (Collection.class.isAssignableFrom(clazz)) {
            return ((Collection<?>) object).stream()
                    .anyMatch((Predicate<Object>) OperateLogAspect::isIgnoreArgs);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return isIgnoreArgs(((Map<?, ?>) object).values());
        }
        // obj
        return object instanceof MultipartFile
                || object instanceof HttpServletRequest
                || object instanceof HttpServletResponse
                || object instanceof BindingResult;
    }

}
