/*
 * System Name         : GEBNexGen
 * Program Id          : gebng-cms
 * Program Description : Toast load jdbc ftl mapper test
 *
 * Revision History
 *
 * Date            Author             SR No           Description of Change
 * ----------      ----------         ------          ----------------------
 * Nov 15, 2019    Iynkaran				R5			  Initial commit
 *
 * Copyright (c) United Overseas Bank Limited Co.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * United Overseas Bank Limited Co. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * United Overseas Bank Limited Co.
 */

package com.ethan;

import com.ethan.config.ExportProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {PropertySourceApplication.class})
@TestPropertySource(properties = {"country_code=CN", "spring.profiles.active=sit"})
public class PropertySourceApplicationTest {
    @Autowired
    private ExportProperties exportProperties;

    @Test
    public void testProperties() {
        String path = exportProperties.getReportTemplatePath();
        System.out.println("--->" + path);
    }
}
