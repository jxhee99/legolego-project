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
public class PackageFormDTO {
  private String packageName;
  private String profileImg;
  private String shortDescription;

  public static PackageFormDTO getDiyEntity(DiyEntity diyEntity) {
    return PackageFormDTO.builder()
            .packageName(diyEntity.getPackageName())
            .profileImg(diyEntity.getProfileImg())
            .shortDescription(diyEntity.getShortDescription())
            .build();
  }
}
