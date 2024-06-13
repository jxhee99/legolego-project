package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiyRepository extends JpaRepository<DiyEntity, Long> {
  @Modifying
  @Transactional
  @Query("UPDATE DiyEntity d SET d.packageViewNum = d.packageViewNum + 1 WHERE d.packageNum = :packageNum")
  void incrementViewNum(@Param("packageNum") Long packageNum);
}
