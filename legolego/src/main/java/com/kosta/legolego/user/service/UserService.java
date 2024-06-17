package com.kosta.legolego.user.service;


import com.kosta.legolego.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.kosta.legolego.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

}


