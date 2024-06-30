package com.kosta.legolego.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileDto {

    private Long adminNum;
    private String adminEmail;
    private String adminName;

}
