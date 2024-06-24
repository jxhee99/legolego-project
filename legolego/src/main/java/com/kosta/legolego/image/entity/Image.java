package com.kosta.legolego.image.entity;

import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "images")
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_num")
    private Long ImageNum;

    @Column(name = "image_url") // null 들어올 수 없게 해야하는데 만약, 이미지가 안 들어올 때를 생각하면 null 허용해야 할 것 같기도
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "detail_course_num")
    private DetailCourseEntity detailCourse;

}
