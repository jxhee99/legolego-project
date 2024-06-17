package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.DiyPackage;
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

  public static DiyDTO toDiyDTO(DiyPackage diyPackage) {
    return DiyDTO.builder()
            .packageName(diyPackage.getPackageName())
            .profileImg(diyPackage.getProfileImg())
            .shortDescription(diyPackage.getShortDescription())
            .build();
  }
}
