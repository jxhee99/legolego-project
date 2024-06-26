package com.kosta.legolego.image.repository;


import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.image.entity.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByDetailCourse(DetailCourseEntity detailCourse);

    @Transactional
    @Query("DELETE FROM Image d WHERE d.detailCourse = :detailCourse")
    @Modifying
    void deleteByDetailCourse(@Param("detailCourse")DetailCourseEntity detailCourse);
}
