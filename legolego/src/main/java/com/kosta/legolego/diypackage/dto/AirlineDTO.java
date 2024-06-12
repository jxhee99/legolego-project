package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirlineDTO {
  private Long airlineNum;
  private String airlineName;
  private String startingPoint;
  private String destination;
  private String startFlightNum;
  private LocalDateTime boardingDate;
  private String comeFlightNum;
  private LocalDateTime comingDate;

  public AirlineEntity toEntity() {
    return AirlineEntity.builder()
            .airlineNum(this.airlineNum)
            .airlineName(this.airlineName)
            .startingPoint(this.startingPoint)
            .destination(this.destination)
            .startFlightNum(this.startFlightNum)
            .boardingDate(this.boardingDate)
            .comeFlightNum(this.comeFlightNum)
            .comingDate(this.comingDate)
            .build();
  }
}
