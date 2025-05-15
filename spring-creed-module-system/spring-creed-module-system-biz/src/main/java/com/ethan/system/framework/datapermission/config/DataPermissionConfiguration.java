package com.ethan.system.framework.datapermission.config;

import org.springframework.context.annotation.Configuration;

/**
 * system 模块的数据权限 Configuration
 *
 * 
 */
@Configuration(proxyBeanMethods = false)
public class DataPermissionConfiguration {

/*     @Bean
    public DeptDataPermissionRuleCustomizer sysDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(AdminUserDO.class);
            rule.addDeptColumn(DeptDO.class, "id");
            // user
            rule.addUserColumn(AdminUserDO.class, "id");
        };
    } */

}
