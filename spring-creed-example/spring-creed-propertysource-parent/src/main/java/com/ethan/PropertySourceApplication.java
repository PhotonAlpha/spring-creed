/**
 * System Name         : GEBNextGen
 * Program Id          : gebng-reports-core
 * Program Description :  Report application entry point
 *
 * @author Le Tam An
 * @date 10-SEP-2019
 * @version 1.0
 *
 * <p>
 * Revision History
 * <p>
 * Date            Author             SR No           Description of Change
 * ----------      ----------         ------          ----------------------
 * 10-SEP-2019     Le Tam An          Release5		  Initial commit
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
package com.ethan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Report application entry point
 *
 * @author Iynkaran
 */

@SpringBootApplication
public class PropertySourceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PropertySourceApplication.class, args);
  }
}
