package com.kosta.legolego.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyProfileDto {

    private Long userNum;
    private String userNickname;
    private String userPhone;
    private String userEmail;
    private String userName;
}
