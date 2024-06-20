package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.RouteEntity;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiyRouteDTO {
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
  public static DiyRouteDTO toRouteDTO(RouteEntity routeEntity) {
    DiyRouteDTO diyRouteDTO = new DiyRouteDTO();
    BeanUtils.copyProperties(routeEntity, diyRouteDTO);
    return diyRouteDTO;
  }
}
