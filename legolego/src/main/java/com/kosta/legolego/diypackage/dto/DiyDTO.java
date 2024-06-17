package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.DiyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DiyDTO {
  private String packageName;
  private String profileImg;
  private String shortDescription;

  public static DiyDTO toDiyDTO(DiyEntity diyEntity) {
    return DiyDTO.builder()
            .packageName(diyEntity.getPackageName())
            .profileImg(diyEntity.getProfileImg())
            .shortDescription(diyEntity.getShortDescription())
            .build();
  }
}
