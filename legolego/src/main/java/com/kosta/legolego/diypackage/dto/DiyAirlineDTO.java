package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiyAirlineDTO {
  private Long airlineNum;
  private String startAirlineName;
  private String startingPoint;
  private String destination;
  private String startFlightNum;
  private LocalDateTime boardingDate;
  private String comeAirlineName;
  private String comeFlightNum;
  private LocalDateTime comingDate;

  public AirlineEntity toEntity() {
    return AirlineEntity.builder()
            .airlineNum(this.airlineNum)
            .startAirlineName(this.startAirlineName)
            .startingPoint(this.startingPoint)
            .destination(this.destination)
            .startFlightNum(this.startFlightNum)
            .boardingDate(this.boardingDate)
            .comeAirlineName(this.comeAirlineName)
            .comeFlightNum(this.comeFlightNum)
            .comingDate(this.comingDate)
            .build();
  }
  public static DiyAirlineDTO toAirlineDTO(AirlineEntity airlineEntity) {
    DiyAirlineDTO diyAirlineDTO = new DiyAirlineDTO();
    BeanUtils.copyProperties(airlineEntity, diyAirlineDTO);
    return diyAirlineDTO;
  }

}
