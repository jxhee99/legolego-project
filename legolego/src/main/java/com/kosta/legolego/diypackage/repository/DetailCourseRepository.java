package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailCourseRepository extends JpaRepository<DetailCourseEntity, Long> {
  List<DetailCourseEntity> findByRoute(RouteEntity route);
}
