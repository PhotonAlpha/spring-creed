/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/01/05
 */
package com.ethan.test.comtroller;

import com.ethan.test.mapper.AuditingEntityMapper;
import com.ethan.test.model.StudentA;
import com.ethan.test.model.StudentB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {
  @Autowired
  private AuditingEntityMapper auditingEntityMapper;

  @GetMapping("/student")
  public ResponseEntity<StudentB> getStudent() {
    StudentA sa = new StudentA();
    sa.setFirst_name("小明");
    sa.setLast_name("wang");

    StudentB sb = auditingEntityMapper.convert(sa);
    return ResponseEntity.ok(sb);
  }
}
