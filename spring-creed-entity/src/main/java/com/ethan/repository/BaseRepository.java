package com.ethan.repository;

import com.ethan.entity.BaseDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseDO, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.active = true")
  Iterable<T> findAll();

  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.active = true")
  Iterable<T> findAll(Sort sort);

  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.active = true")
  Page<T> findAll(Pageable pageable);

  @Override
  <S extends T> S save(S s);

  @Override
  <S extends T> Iterable<S> saveAll(Iterable<S> iterable);

  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.id = ?1 and e.active = true")
  Optional<T> findById(ID id);

  @Override
  default boolean existsById(ID id) {
    return findById(id) != null;
  }

  @Override
  @Transactional(readOnly = true)
  @Query("select count(e) from #{#entityName} e where e.active = true")
  long count();

  @Query("update #{#entityName} e set e.deleted = true where e.id = ?1")
  @Transactional
  @Modifying
  void logicDelete(ID id);

  @Transactional
  default void logicDelete(T entity) {

  }

  @Transactional
  default void logicDelete(Iterable<? extends T> entities) {

  }

  @Query("update #{#entityName} e set e.deleted = false ")
  @Transactional
  @Modifying
  void logicDeleteAll();
}
