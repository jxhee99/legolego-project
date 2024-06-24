package com.kosta.legolego.image.repository;


import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByDetailCourse(DetailCourseEntity detailCourse);
}
