package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiyDTO {
  private Long packageNum;
  private String packageName;
  private String profileImg;
  private LocalDate regDate;
  private LocalDate modDate;
  private int packageLikedNum;
  private int packageViewNum;
  private String shortDescription;
  private AirlineEntity airline;
  private RouteEntity route;
  //임시 타입, 해당 테이블 entity 생기면 수정
  private Long userNum;


  public DiyEntity toEntity() {
    return DiyEntity.builder()
            .packageNum(this.packageNum)
            .packageName(this.packageName)
            .profileImg(this.profileImg)
            .regDate(this.regDate != null ? this.regDate : LocalDate.now())
            .modDate(this.modDate)
            .packageLikedNum(this.packageLikedNum)
            .packageViewNum(this.packageViewNum)
            .shortDescription(this.shortDescription)
            .userNum(this.userNum)
            .airline(this.airline)
            .route(this.route)
            .build();
  }
}
