package com.kosta.legolego.member.controller;

import com.kosta.legolego.member.dto.*;
import com.kosta.legolego.member.service.AuthService;
import com.kosta.legolego.member.service.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private EmailService emailService;

    // 회원가입
//    @PostMapping("/signup")
//    public ResponseEntity<?> signupUser(@Valid @RequestBody SignupDto signupDto,
//                                        @RequestParam(name = "role") String role) {
//        try {
//            return ResponseEntity.ok(authService.signup(signupDto, role));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
    // 회원가입 - 이메일 인증
    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@Valid @RequestBody SignupDto signupDto,
                                        @RequestParam(name = "role") String role) {
        try {
            ResponseDto savedUserDto = authService.signup(signupDto, role);

            // 이메일 인증 토큰 생성 및 세션에 저장
            String token = UUID.randomUUID().toString();
            httpSession.setAttribute("emailVerificationToken", token);
            httpSession.setAttribute("emailVerificationUser", savedUserDto.getEmail());

            // 이메일 인증 메일 전송
            emailService.sendEmailVerificationEmail(savedUserDto.getEmail(), token);

            return ResponseEntity.ok(savedUserDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 이메일 인증 처리
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String sessionToken = (String) httpSession.getAttribute("emailVerificationToken");
        String email = (String) httpSession.getAttribute("emailVerificationUser");

        log.info("Received token: {}", token);
        log.info("Session token: {}", sessionToken);

        if (sessionToken != null && sessionToken.equals(token)) {
            authService.enableUser(email);
            httpSession.removeAttribute("emailVerificationToken");
            httpSession.removeAttribute("emailVerificationUser");
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        try {
            Map<String, String> tokens = authService.login(loginDto);
            String refreshToken = tokens.get("refreshToken");

            // HTTPOnly 쿠키에 리프레시 토큰 저장
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);  // HTTPS에서만 사용하도록 설정. 개발 중에는 false로 설정
            refreshTokenCookie.setPath("/");  // 전체 도메인에서 사용
            refreshTokenCookie.setMaxAge((int) (authService.getRefreshTokenValidity() / 1000));
            response.addCookie(refreshTokenCookie);

            // 응답에 Access Token 추가
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", tokens.get("accessToken"));
            responseBody.put("role", authService.getRole(loginDto.getEmail()));
            responseBody.put("memberId", tokens.get("memberId")); // 반환값 memberId 추가

            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 토큰 갱신 엔드포인트
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken);

        // HTTPOnly 쿠키에서 리프레시 토큰 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);  // HTTPS에서만 사용하도록 설정. 개발 중에는 false 설정
        refreshTokenCookie.setPath("/");  // 전체 도메인에서 사용
        refreshTokenCookie.setMaxAge(0);  // 쿠키 삭제
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("로그아웃 되었습니다.");
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

