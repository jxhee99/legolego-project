package com.kosta.legolego.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 사용해주세요.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호를 확인해주세요.")
    @Size(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하로 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "이름에는 특수 문자를 포함할 수 없습니다.")
    private String name;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임에는 특수 문자를 포함할 수 없습니다.")
    private String nickname; // User만 사용

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대전화 번호는 000-0000-0000 형식으로 입력해주세요.")
    @Size(min = 13, max = 15, message = "휴대전화 번호는 13자 이상 15자 이하로 입력해주세요.")
    private String phone;

    @NotBlank(message = "회사명을 입력해주세요.")
    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "특수 문자를 포함할 수 없습니다.")
    private String companyName; // Partner만 사용
}
