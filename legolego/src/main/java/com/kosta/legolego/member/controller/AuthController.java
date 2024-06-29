package com.kosta.legolego.member.controller;

import com.kosta.legolego.member.dto.*;
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

    // 아이디 찾기 - 일반 회원
    @GetMapping("/find-user-email")
    public ResponseEntity<String> findUserEmail(@RequestParam(name = "userName") String userName,
                                                @RequestParam(name = "userPhone") String userPhone) {
        String email = authService.findUserEmail(userName, userPhone);
        if (email != null) {
            return ResponseEntity.ok().body(email);
        } else {
            return ResponseEntity.status(404).body("가입된 정보를 찾을 수 없습니다.");
        }
    }

    // 아이디 찾기 - 여행사
    @GetMapping("/find-partner-email")
    public ResponseEntity<String> findPartnerEmail(@RequestParam(name = "companyName") String companyName,
                                                   @RequestParam(name = "partnerPhone") String partnerPhone) {
        String email = authService.findPartnerEmail(companyName, partnerPhone);
        if (email != null) {
            return ResponseEntity.ok().body(email);
        } else {
            return ResponseEntity.status(404).body("가입된 정보를 찾을 수 없습니다.");
        }
    }

    // 비밀번호 찾기
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody FindPasswordRequestDto findPasswordRequestDto) {
        return authService.requestPasswordReset(findPasswordRequestDto);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> validateResetToken(@RequestParam String token) {
        return authService.validateResetToken(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return authService.resetPassword(resetPasswordRequestDto.getToken(), resetPasswordRequestDto);
    }
}

