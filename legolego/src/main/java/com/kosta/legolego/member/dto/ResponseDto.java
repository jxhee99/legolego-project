package com.kosta.legolego.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

    private Long id;  // 각 엔티티의 식별자를 포함
    private String email;
    private String name;
    private String nickname;
    private String phone;
    private String companyName;
}
