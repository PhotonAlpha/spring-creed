/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/01/03
 */
package com.ethan.test.mapper;

import com.ethan.test.model.StudentA;
import com.ethan.test.model.StudentB;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AuditingEntityMapper {

  @Mappings({
      @Mapping(target = "firstName", source = "first_name"),
      @Mapping(target = "lastName", source = "last_name"),
      @Mapping(target = "age", source = "age")
  })
  StudentB convert(StudentA studentA);
}
