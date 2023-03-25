package com.ethan.system.controller.admin.captcha;

import com.ethan.common.common.R;
import com.ethan.system.controller.admin.captcha.vo.CaptchaImageRespVO;
import com.ethan.system.service.captcha.CaptchaService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 验证码")
@RestController
@RequestMapping("/system/captcha")
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    @GetMapping("/get-image")
    @PermitAll
    @Schema(name = "生成图片验证码")
    public R<CaptchaImageRespVO> getCaptchaImage() {
        return R.success(captchaService.getCaptchaImage());
    }

}
