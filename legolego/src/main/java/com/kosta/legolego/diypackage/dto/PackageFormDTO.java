package com.kosta.legolego.diypackage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PackageFormDTO {
  private String packageName;
  private String profileImg;
  private String shortDescription;
}
