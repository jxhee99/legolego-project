package com.kosta.legolego.member.controller;

import com.kosta.legolego.member.dto.LoginDto;
import com.kosta.legolego.member.dto.ResponseDto;
import com.kosta.legolego.member.dto.SignupDto;
import com.kosta.legolego.member.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseDto signupUser(@RequestBody SignupDto signupDto,
                                  @RequestParam(name = "role") String role) {
        return authService.signup(signupDto, role);
    }

    // 로그인
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logoutUser() {
        // 클라이언트 측에서 JWT 토큰 삭제
        return "로그아웃 되었습니다!";  // 이거 수정 확인 return authService.logout();
    }

    // 아이디 찾기
    @GetMapping("/find-email")
    public String findEmail(@RequestParam("name") String name,
                            @RequestParam("phone") String phone) {
        return authService.findEmail(name, phone);
    }

//    // 비밀번호 재설정 요청
//    @PostMapping("/reset-password")
//    public void resetPassword(@RequestParam("email") String email,
//                              @RequestParam("name") String name,
//                              @RequestParam("phone") String phone) {
//        authService.resetPassword(email, name, phone);
//    }
//
//    // 비밀번호 업데이트
//    @PostMapping("/update-password")
//    public void updatePassword(@RequestParam("token") String token,
//                               @RequestParam("newPassword") String newPassword) {
//        authService.updatePassword(token, newPassword);
//    }
}
