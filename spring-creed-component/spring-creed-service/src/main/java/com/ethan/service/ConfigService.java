package com.ethan.service;

import com.ethan.model.AppDictionary;

import java.util.List;
import java.util.Optional;

public interface ConfigService {
  String play(Long appId, String type, String operator);
  String playEvict(Long appId, String type, String operator);

  List<String> put(String content);

  Optional<AppDictionary> getOne(Long id);
  List<AppDictionary> getAll();

  boolean insertDic(AppDictionary entity);
  boolean updateDic(AppDictionary entity);
  boolean deleteDic(Long id);
}
