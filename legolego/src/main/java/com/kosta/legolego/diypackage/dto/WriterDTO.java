package com.kosta.legolego.diypackage.dto;

import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriterDTO {
  private String userName;
  private String userNickname;

  public static WriterDTO toWriterDTO(DiyPackage diyPackage) {
    return WriterDTO.builder()
            .userNickname(diyPackage.getUser().getUserNickname())
            .userName(diyPackage.getUser().getUserName())
            .build();
  }
}
