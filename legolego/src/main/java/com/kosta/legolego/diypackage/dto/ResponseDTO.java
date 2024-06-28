package com.kosta.legolego.diypackage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kosta.legolego.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Builder
@Data
public class ResponseDTO {
  private DiyAirlineDTO airline;
  private DiyRouteDTO route;
  private List<DiyDetailCourseDTO> detailCourses;
  private DiyDTO packageForm;
  private WriterDTO user;
  private LocalDate regDate;
  private int likedNum;
  private int viewNum;
  @JsonProperty("isLiked")
  private boolean isLiked;
  @JsonProperty("isWriter")
  private boolean isWriter;
}
