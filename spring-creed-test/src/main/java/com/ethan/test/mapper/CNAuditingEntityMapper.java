package com.ethan.test.mapper;

import com.ethan.test.model.StudentA;
import com.ethan.test.model.StudentB;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("CNAuditingEntityMapperImpl")
@Primary
public class CNAuditingEntityMapper extends AuditingEntityMapperImpl {
  @Override
  public StudentB convert(StudentA studentA) {
    StudentB sb = super.convert(studentA);
    sb.setAge("123");
    return sb;
  }
}
