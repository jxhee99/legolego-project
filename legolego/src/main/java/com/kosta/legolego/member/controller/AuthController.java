package com.kosta.legolego.member.controller;

import com.kosta.legolego.member.dto.LoginDto;
import com.kosta.legolego.member.dto.ResponseDto;
import com.kosta.legolego.member.dto.SignupDto;
import com.kosta.legolego.member.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 회원가입
    @PostMapping("/signup")
//    public ResponseDto signupUser(@Valid @RequestBody SignupDto signupDto,
//                                  @RequestParam(name = "role") String role) {
//        return authService.signup(signupDto, role);
//    }
    public ResponseEntity<?> signupUser(@Valid @RequestBody SignupDto signupDto,
                                        @RequestParam(name = "role") String role) {
        try {
            return ResponseEntity.ok(authService.signup(signupDto, role));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginDto loginDto) {
        try {
            String token = authService.login(loginDto);
            String role = authService.getRole(loginDto.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logoutUser() {
        // 클라이언트 측에서 JWT 토큰 삭제
        return "로그아웃 되었습니다!";  // 이거 수정 확인 return authService.logout();
    }

    // 유효성 검사 - 닉네임 중복
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam(name = "nickname") String nickname) {
        boolean isAvailable = authService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }

    // 유효성 검사 - 이메일 중복
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam(name = "email") String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        return ResponseEntity.ok(isAvailable);
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
