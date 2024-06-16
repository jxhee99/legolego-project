package com.kosta.legolego.diypackage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

  @Column(name = "day_num", nullable = false )
  private LocalDate dayNum;

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

  @Column(name = "file_url", nullable = false)
  private String fileUrl;

  @ManyToOne
  @JoinColumn(name = "route_num", nullable = false)
  private RouteEntity route;

  //코스 필드 초기화
  public void clearCourses() {
    this.course1 = null;
    this.course2 = null;
    this.course3 = null;
    this.course4 = null;
    this.course5 = null;
    this.course6 = null;
    this.course7 = null;
    this.course8 = null;
    this.course9 = null;
    this.course10 = null;
  }

  // 동적 필드 설정 메소드
  public void setCourse(int courseNumber, String course) {
    switch (courseNumber) {
      case 1: this.course1 = course; break;
      case 2: this.course2 = course; break;
      case 3: this.course3 = course; break;
      case 4: this.course4 = course; break;
      case 5: this.course5 = course; break;
      case 6: this.course6 = course; break;
      case 7: this.course7 = course; break;
      case 8: this.course8 = course; break;
      case 9: this.course9 = course; break;
      case 10: this.course10 = course; break;
      default: throw new IllegalArgumentException("유효하지않은 번호 " + courseNumber);
    }
  }
}
