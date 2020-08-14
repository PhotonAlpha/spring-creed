/**
 * System Name         : GEBNextGen
 * Program Id          : gebng-export-core
 * Program Description : Reporting microservice properties
 *
 * @author venlaf
 * @date 13-JAN-2020
 * @version 1.0
 *
 * <p>
 * Revision History
 * <p>
 * Date            Author             SR No             Description of Change
 * ----------      ----------         ------            ----------------------
 * 13-JAN-2020     venlaf             Release5.2.0		 Initial commit
 * <p>
 * @copyright Copyright (c) United Overseas Bank Limited Co.
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of
 * United Overseas Bank Limited Co. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * United Overseas Bank Limited Co.
 */
package com.ethan.config;

import com.ethan.factory.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/**
 * Reporting properties
 *
 * @author venlaf
 */
@Component
@PropertySources(value = {
        @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:property-context.yml"),
        @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:property-context-${country_code}.yml", ignoreResourceNotFound = true)
})
@ConfigurationProperties(prefix = "rules")
@EnableConfigurationProperties
@Getter
@Setter
public class ExportProperties {

    @Value("${gng-export-ms-cluster.protocol}")
    private String exportWrapperProtocol;

    String reportTemplatePath;
}
