package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.RouteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDTO {
  private Long routeNum;
  private LocalDate startDate;
  private LocalDate lastDate;

  public RouteEntity toEntity() {
    return RouteEntity.builder()
            .routeNum(this.routeNum)
            .startDate(this.startDate)
            .lastDate(this.lastDate)
            .build();
  }
}
