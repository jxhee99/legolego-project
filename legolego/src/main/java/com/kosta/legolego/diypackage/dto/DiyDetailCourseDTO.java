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
    DetailCourseEntity detailCourseEntity = DetailCourseEntity.builder()
            .route(routeEntity)
            .dayNum(this.dayNum)
            .fileUrl(this.fileUrl)
            .build();

    // 코스 수 만큼 setCourse
    for (int i = 0; i < this.courses.size(); i++) {
      detailCourseEntity.setCourse(i + 1, this.courses.get(i));
    }

    return detailCourseEntity;
  }

  public static List<DiyDetailCourseDTO> toDetailCourseDTOList(List<DetailCourseEntity> detailCourseEntities) {
    return detailCourseEntities.stream()
            .map(detailCourse -> {
              DiyDetailCourseDTO dto = DiyDetailCourseDTO.builder()
                      .detailCourseNum(detailCourse.getDetailCourseNum())
                      .dayNum(detailCourse.getDayNum())
                      .fileUrl(detailCourse.getFileUrl())
                      .build();

              // 필드를 리스트로 변환하고 널 필드 제거
              List<String> courses = Arrays.asList(
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
                      ).stream()
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());

              dto.setCourses(courses);
              return dto;
            })
            .collect(Collectors.toList());
  }
}
