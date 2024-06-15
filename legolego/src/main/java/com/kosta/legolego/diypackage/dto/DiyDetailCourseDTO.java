package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiyDetailCourseDTO {
  private Long detailCourseNum;
  //private RouteEntity route;
  //private Long routeNum;
  private LocalDate dayNum;
  private List<String> courses; // 코스를 담을 리스트
  private String fileUrl;

  public DetailCourseEntity toEntity(RouteEntity routeEntity) {
    return DetailCourseEntity.builder()
            .route(routeEntity)
            .dayNum(this.dayNum)
            .course1(this.courses.get(0))
            .course2(this.courses.get(1))
            .course3(this.courses.get(2))
            .course4(this.courses != null && this.courses.size() > 3 ? this.courses.get(3) : null)
            .course5(this.courses != null && this.courses.size() > 4 ? this.courses.get(4) : null)
            .course6(this.courses != null && this.courses.size() > 5 ? this.courses.get(5) : null)
            .course7(this.courses != null && this.courses.size() > 6 ? this.courses.get(6) : null)
            .course8(this.courses != null && this.courses.size() > 7 ? this.courses.get(7) : null)
            .course9(this.courses != null && this.courses.size() > 8 ? this.courses.get(8) : null)
            .course10(this.courses != null && this.courses.size() > 9 ? this.courses.get(9) : null)
            .fileUrl(this.fileUrl)
            .build();
  }

  public static List<DiyDetailCourseDTO> toDetailCourseDTOList(List<DetailCourseEntity> detailCourseEntities) {
    return detailCourseEntities.stream()
            .map(detailCourse -> DiyDetailCourseDTO.builder()
                    .detailCourseNum(detailCourse.getDetailCourseNum())
                    .dayNum(detailCourse.getDayNum())
                    .courses(Arrays.asList(
                            detailCourse.getCourse1(),
                            detailCourse.getCourse2(),
                            detailCourse.getCourse3(),
                            detailCourse.getCourse4(),
                            detailCourse.getCourse5(),
                            detailCourse.getCourse6(),
                            detailCourse.getCourse7(),
                            detailCourse.getCourse8(),
                            detailCourse.getCourse9(),
                            detailCourse.getCourse10()
                    ).stream().filter(Objects::nonNull).collect(Collectors.toList()))
                    .fileUrl(detailCourse.getFileUrl())
                    .build())
            .collect(Collectors.toList());
  }
}
