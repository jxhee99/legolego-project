package com.kosta.legolego.user.service;


import com.kosta.legolego.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.kosta.legolego.user.entity.SiteUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String userEmail, String userPw, String username, String userNickname, String userPhone) {
        SiteUser user = new SiteUser();
        user.setUserEmail(userEmail);
        user.setUserPw(passwordEncoder.encode(userPw));
        user.setUsername(username);
        user.setUserNickname(userNickname);
        user.setUserPhone(userPhone);
        user.setUserStatus(SiteUser.UserStatus.registered);

        this.userRepository.save(user);
        return saveSiteUser(user); // saveSiteUser 메서드 호출하여 저장

    }


    // 새로 추가할 saveSiteUser 메서드
    public SiteUser saveSiteUser(SiteUser siteUser) {
        if (siteUser.getUserNickname() == null || siteUser.getUsername() == null || siteUser.getUserPhone() == null) {
            throw new IllegalArgumentException("User nickname, username, and user phone must not be null");
        }
        return userRepository.save(siteUser);
    }



    public SiteUser findByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

}


