package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.user.entity.User;
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
  private User user;
  private int likedNum;
  private int viewNum;
  private boolean isLiked;
}
