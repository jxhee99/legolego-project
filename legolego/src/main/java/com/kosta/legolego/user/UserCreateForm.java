package com.kosta.legolego.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {

    @NotEmpty(message ="이메일 주소를 입력해주세요")
    @Email
    private String userEmail;


    @Size(min =8, max=25)
    @NotEmpty(message="사용하실 비밀번호를 입력해주세요")
    private String userPw1;

    @Size(min =8, max=25)
    @NotEmpty(message="비밀번호 확인을 입력해주세요")
    private String userPw2;

    @Size(min =2, max=10)
    @NotEmpty(message="이름을 입력해주세요")
    private String userName;

    @Size(min=1, max=10)
    @NotEmpty(message="사용하실 닉네임을 입력해주세요")
    private String userNickname;

    @Size(min=10, max=11)
    @NotEmpty(message="휴대폰 번호를 입력해주세요")
    private String userPhone;

}
