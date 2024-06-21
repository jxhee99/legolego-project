package com.kosta.legolego.user.service;


import com.kosta.legolego.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.kosta.legolego.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    public User create(String userEmail, String userPw, String userName, String userNickname, String userPhone) {
        User user = new User();
        user.setUserEmail(userEmail);
        user.setUserPw(passwordEncoder.encode(userPw));
        user.setUserName(userName);
        user.setUserNickname(userNickname);
        user.setUserPhone(userPhone);
        user.setUserStatus(User.UserStatus.registered);

        this.userRepository.save(user);
        return saveSiteUser(user); // saveSiteUser 메서드 호출하여 저장

    }


    // 새로 추가할 saveSiteUser 메서드
    public User saveSiteUser(User user) {
        if (user.getUserNickname() == null || user.getUserName() == null || user.getUserPhone() == null) {
            throw new IllegalArgumentException("User nickname, userName, and user phone must not be null");
        }
        return userRepository.save(user);
    }



    public User findByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public String findUserEmail(String userName, String userPhone) {
        Optional<User> userOptional = userRepository.findByUserNameAndUserPhone(userName, userPhone);
        if (userOptional.isPresent()) {
            String userEmail = userOptional.get().getUserEmail();
            return maskEmail(userEmail);
        }
        return null;
    }

    private String maskEmail(String userEmail) {
        int atIndex = userEmail.indexOf("@");
        if (atIndex > 3) {
            String prefix = userEmail.substring(0, 3);
            String domain = userEmail.substring(atIndex);
            return prefix + "*".repeat(atIndex - 3) + domain;
        }
        return userEmail;
    }

    //비밀번호 변경 메일보내기
        private Map<String, User> users = new HashMap<>();
        private Map<String, String> tokenRepository = new HashMap<>();

        public User findUserByUserEmail(String Useremail) {
            return users.get(Useremail);
        }

        public void createUserPwResetTokenForUser(User user, String token) {
            tokenRepository.put(token, user.getUserEmail());
        }

        public User getUserByUserPwResetToken(String token) {
            String userEmail = tokenRepository.get(token);
            return users.get(userEmail);
        }



    //비밀번호 재설정하기
        public void resetPassword(String uuid, String newPassword) {
            // Redis에서 이메일 주소 가져오기
            String userEmail = redisService.getValues(uuid);
            if (userEmail == null) {
                throw new RuntimeException("유효하지 않은 토큰입니다.");
            }

            // 사용자 조회
            User user = userRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("등록되지 않은 사용자입니다: " + userEmail));

            // 비밀번호 변경
            user.setUserPw(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Redis에서 UUID 삭제
            redisService.deleteValues(uuid);
        }
}


