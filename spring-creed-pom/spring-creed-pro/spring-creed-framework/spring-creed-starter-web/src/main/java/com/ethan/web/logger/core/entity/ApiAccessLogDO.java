package com.ethan.web.logger.core.entity;

import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * API 访问日志
 *
 * @author 芋道源码
 */
@Entity
@Table(name = "infra_api_access_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "enabled", column = @Column(name = "deleted"))
public class ApiAccessLogDO extends BaseDO {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 链路追踪编号
     *
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。
     */
    private String traceId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * 应用名
     *
     * 目前读取 `spring.application.name` 配置项
     */
    private String applicationName;

    // ========== 请求相关字段 ==========

    /**
     * 请求方法名
     */
    private String requestMethod;
    /**
     * 访问地址
     */
    private String requestUrl;
    /**
     * 请求参数
     *
     * query: Query String
     * body: Quest Body
     */
    private String requestParams;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 浏览器 UA
     */
    private String userAgent;

    // ========== 执行相关字段 ==========

    /**
     * 开始请求时间
     */
    private Date beginTime;
    /**
     * 结束请求时间
     */
    private Date endTime;
    /**
     * 执行时长，单位：毫秒
     */
    private Integer duration;
    /**
     * 结果码
     *
     * 目前使用的 {@link CommonResult#getCode()} 属性
     */
    private Integer resultCode;
    /**
     * 结果提示
     *
     * 目前使用的 {@link CommonResult#getMsg()} 属性
     */
    private String resultMsg;


}
