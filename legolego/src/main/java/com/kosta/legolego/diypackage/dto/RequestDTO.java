package com.kosta.legolego.diypackage.dto;

import lombok.Data;

import java.util.List;


@Data
public class RequestDTO {
  private AirlineDTO airline;
  private RouteDTO route;
  private List<DetailCourseDTO> detailCourses;
  private PackageFormDTO packageForm;
  private Long userNum;
}
