package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyPackage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiyRepository extends JpaRepository<DiyPackage, Long> {
  @Modifying
  @Transactional
  @Query("UPDATE DiyPackage d SET d.packageViewNum = d.packageViewNum + 1 WHERE d.packageNum = :packageNum")
  void incrementViewNum(@Param("packageNum") Long packageNum);

  //최신등록순 정렬
  List<DiyPackage> findAllByOrderByPackageNumDesc();
}
