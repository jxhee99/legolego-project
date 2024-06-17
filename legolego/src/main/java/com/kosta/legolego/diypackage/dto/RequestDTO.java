package com.kosta.legolego.diypackage.dto;

import lombok.Data;

import java.util.List;


@Data
public class RequestDTO {
  private DiyAirlineDTO airline;
  private DiyRouteDTO route;
  private List<DiyDetailCourseDTO> detailCourses;
  private DiyDTO packageForm;
  private Long userNum;
}
