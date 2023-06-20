package com.ethan.system.controller.admin.captcha;

import cn.hutool.core.util.StrUtil;
import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.common.utils.servlet.ServletUtils;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.system.controller.admin.captcha.vo.CaptchaVO;
import com.ethan.system.controller.admin.captcha.vo.ResponseModel;
import com.ethan.system.service.captcha.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag(name = "管理后台 - 验证码")
@RestController
@RequestMapping("/system/captcha")
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

/*     @GetMapping("/get-image")
    @PermitAll
    @Schema(name = "生成图片验证码")
    public R<CaptchaImageRespVO> getCaptchaImage() {
        return R.success(captchaService.getCaptchaImage());
    } */

    @PostMapping({"/get"})
    @Operation(summary = "获得验证码")
    @PermitAll
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) throws IOException {
        assert request.getRemoteHost() != null;
        data.setBrowserInfo(getRemoteId(request));
        String str = IOUtils.toString(new ClassPathResource("captcha/mock/getCaptcha.json").getInputStream(), StandardCharsets.UTF_8);
        // return captchaService.get(data);
        return JacksonUtils.parseObject(str, ResponseModel.class);
    }

    @PostMapping("/check")
    @Operation(summary = "校验验证码")
    @PermitAll
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) throws IOException {
        data.setBrowserInfo(getRemoteId(request));
        String str = IOUtils.toString(new ClassPathResource("captcha/mock/checkCaptcha.json").getInputStream(), StandardCharsets.UTF_8);
        // return captchaService.get(data);
        return JacksonUtils.parseObject(str, ResponseModel.class);
    }

    public static String getRemoteId(HttpServletRequest request) {
        String ip = ServletUtils.getClientIP(request);
        String ua = request.getHeader("user-agent");
        if (StrUtil.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

}
