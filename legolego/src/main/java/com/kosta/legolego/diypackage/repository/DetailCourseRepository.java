package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetailCourseRepository extends JpaRepository<DetailCourseEntity, Long> {
  List<DetailCourseEntity> findByRoute(RouteEntity route);
  @Transactional
  @Query("DELETE FROM DetailCourseEntity d WHERE d.route = :route")
  @Modifying
  void deleteByRoute(@Param("route")RouteEntity route);
}
