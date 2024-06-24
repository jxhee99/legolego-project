package com.kosta.legolego.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto {

    private String email;
    private String password;
    private String name;
    private String nickname; // User만 사용
    private String phone;
    private String companyName; // Partner만 사용
}
