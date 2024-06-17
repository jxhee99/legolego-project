package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.entity.DiyLikeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiyLikeDTO {
  private Long likeNum;
  private Long userNum;
  private Long packageNum;

  public DiyLikeEntity toEntity(DiyEntity diyEntity) {
    return DiyLikeEntity.builder()
            .likeNum(this.likeNum)
            .userNum(this.userNum)
            .diy(diyEntity)
            .build();
  }
}
