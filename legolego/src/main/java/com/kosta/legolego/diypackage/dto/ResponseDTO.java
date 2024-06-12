package com.kosta.legolego.diypackage.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class ResponseDTO {
  private AirlineDTO airline;
  private RouteDTO route;
  private List<DetailCourseDTO> detailCourses;
  private PackageFormDTO packageForm;
  private int userNum;
}
