package com.kosta.legolego.member.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 비밀번호 재설정
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("LegoLego : 비밀번호 재설정 안내입니다.");
            helper.setText("<p>비밀번호 재설정을 위해 아래 링크를 클릭해주세요:</p>" +
                    "<a href=\"" + resetLink + "\">비밀번호 재설정 하러가기</a>", true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 회원가입 - 이메일 인증
    public void sendEmailVerificationEmail(String toEmail, String token) {
        String verificationLink = "http://localhost:5173/verify-email?token=" + token;
        log.info("Sending email verification link: {}", verificationLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("LegoLego : 이메일 인증 안내입니다.");
            helper.setText("<p>이메일 인증을 위해 아래 링크를 클릭해주세요:</p>" +
                    "<a href=\"" + verificationLink + "\">이메일 인증 하러가기</a>", true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
