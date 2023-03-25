package com.ethan.system.service.member;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Member Service 实现类
 *
 * 
 */
@Service
public class MemberServiceImpl implements MemberService, ApplicationContextAware {

    @Value("${yudao.info.base-package:com.ethan}")
    private String basePackage;

    private volatile Object memberUserApi;
    private ApplicationContext applicationContext;

    @Override
    public String getMemberUserMobile(Long id) {
        if (id == null) {
            return null;
        }
        Object user = ReflectUtil.invoke(getMemberUserApi(), "getUser", id);
        if (user == null) {
            return null;
        }
        return ReflectUtil.invoke(user, "getMobile");
    }

    private Object getMemberUserApi() {
        if (memberUserApi == null) {
            memberUserApi = applicationContext.getBean(ClassUtil.loadClass(String.format("%s.module.member.api.user.MemberUserApi", basePackage)));
        }
        return memberUserApi;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
