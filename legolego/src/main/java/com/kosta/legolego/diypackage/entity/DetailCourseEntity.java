package com.kosta.legolego.diypackage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detail_course")
@Data
public class DetailCourseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "detail_course_num")
  private Long detailCourseNum;

  @Column(name = "day_num" )
  private Date dayNum;

  @Column(name = "course_1" ,nullable = false, length = 255)
  private String course1;

  @Column(name = "course_2" ,nullable = false, length = 255)
  private String course2;

  @Column(name = "course_3" ,nullable = false, length = 255)
  private String course3;

  @Column(name = "course_4" ,length = 255)
  private String course4;

  @Column(name = "course_5" ,length = 255)
  private String course5;

  @Column(name = "course_6" ,length = 255)
  private String course6;

  @Column(name = "course_7" ,length = 255)
  private String course7;

  @Column(name = "course_8",length = 255)
  private String course8;

  @Column(name = "course_9",length = 255)
  private String course9;

  @Column(name = "course_10" ,length = 255)
  private String course10;

  @Column(name = "file_url")
  private String fileUrl;

  @ManyToOne
  @JoinColumn(name = "route_num", nullable = false)
  private RouteEntity route;
}
