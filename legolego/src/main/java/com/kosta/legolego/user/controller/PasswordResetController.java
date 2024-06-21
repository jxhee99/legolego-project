package com.kosta.legolego.user.controller;

import com.kosta.legolego.user.service.PasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/user")
public class PasswordResetController {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    //비밃번호 재설정메일을 보내는 페이지
    @PostMapping("/find-password")
    public ResponseEntity<String> requestResetPassword(@RequestParam("userEmail") String userEmail) {
        passwordResetService.sendPasswordResetMail(userEmail);
        logger.info("Password reset request received for email: {}", userEmail);
        return ResponseEntity.status(HttpStatus.OK).body("user/email_success");
    }

    //메일 통해서 들어온 비밀번호 재설정 페이지
    @GetMapping("/reset-password")
    public  ResponseEntity<String>  showResetPasswordForm(@RequestParam("token") String token, Model model) {
        logger.info("Received request to show reset password form for token: {}", token);
        model.addAttribute("token", token);
        return ResponseEntity.status(HttpStatus.OK).body("user/reset-password");

    }

    // 비밀번호 변경 처리
    @PostMapping("/reset-password")
    public  ResponseEntity<String>  resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        logger.info("Received request to reset password for token: {}", token);
        if (!password.equals(confirmPassword)) {
            logger.error("Passwords do not match for token: {}", token);
            model.addAttribute("error", "Passwords do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user/reset-password");
         // 비밀번호 불일치 오류 페이지
        }

        boolean resetSuccessful = passwordResetService.resetPassword(token, password);

        if (resetSuccessful) {
            return ResponseEntity.status(HttpStatus.OK).body("user/reset-password-success");
            // 비밀번호 재설정 성공 페이지
        } else {
            logger.error("Invalid or expired token: {}", token);
            model.addAttribute("error", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user/reset-password");
            // 토큰이 유효하지 않거나 만료된 경우 오류 페이지
        }
    }
}
