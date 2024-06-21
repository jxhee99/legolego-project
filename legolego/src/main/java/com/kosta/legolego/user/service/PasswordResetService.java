package com.kosta.legolego.user.service;

import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public PasswordResetService(RedisService redisService, UserRepository userRepository, JavaMailSender mailSender) {
        this.redisService = redisService;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public void sendPasswordResetMail(String userEmail) {
        try {
            // URL 디코딩 수행
            String decodedEmail = URLDecoder.decode(userEmail, StandardCharsets.UTF_8.toString());

            Optional<User> userOptional = userRepository.findByUserEmail(decodedEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = UUID.randomUUID().toString();



                String resetLink = "http://localhost:8080/user/reset-password?token=" + token;
                sendEmail(decodedEmail, resetLink); // 디코딩된 이메일 주소 사용
                saveUuidAndEmail(token, decodedEmail); // Redis에 uuid와 이메일 저장
            } else {
                logger.error("User not found with email: {}", decodedEmail);
            }
        } catch (Exception e) {
            logger.error("Error sending password reset mail", e);
        }
    }

    private void sendEmail(String to, String resetLink) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            helper.setText("<p>You have requested to reset your password.</p>" +
                    "<p>Click the link below to reset your password:</p>" +
                    "<p><a href=\"" + resetLink + "\">Reset Password</a></p>", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Error sending email", e);
        }
    }

    private void saveUuidAndEmail(String uuid, String email) {
        long uuidValidTime = 3 * 60 * 1000L; // 3분
//        long uuidValidTime = 60 * 60 * 24 * 1000L; // 24시간
        redisService.setValuesWithTimeout(uuid, email, uuidValidTime);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean resetPassword(String uuid, String newPassword) {
        // Redis에서 UUID가 있는지 없는지 확인
        String userEmail = redisService.getValues(uuid);
        if (userEmail == null) {
            return false; // UUID에 해당하는 이메일이 Redis에 없는 경우 처리
        }

        // 이메일을 기반으로 사용자 찾기
        Optional<User> userOptional = userRepository.findByUserEmail(userEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPassword = passwordEncoder.encode(newPassword); // 비밀번호 암호화
            user.setUserPw(encodedPassword); // 암호화된 비밀번호 설정// 토큰 초기화
            userRepository.save(user);
            redisService.deleteValues(uuid); // Redis에서 UUID 제거
            return true; // 비밀번호 변경 성공
        } else {
            throw new RuntimeException("User not found with email: " + userEmail);
        }
    }
}

