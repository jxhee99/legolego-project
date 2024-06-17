package com.kosta.legolego.diypackage.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class ResponseDTO {
  private DiyAirlineDTO airline;
  private DiyRouteDTO route;
  private List<DiyDetailCourseDTO> detailCourses;
  private DiyDTO packageForm;
  private Long userNum;
  private int likedNum;
  private int viewNum;
  private boolean isLiked;
}
