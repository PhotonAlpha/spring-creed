package com.ethan.dao;

import com.ethan.model.AppDictionary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppDictionaryDao extends CrudRepository<AppDictionary, Long> {
  @Query("select dic from AppDictionary dic where dic.key = ?1")
  List<AppDictionary> findByKey(String key);
}
