package com.kosta.legolego.admin.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String companyName;
    private String phone;
}
