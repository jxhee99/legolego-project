package com.kosta.legolego.user.repository;

import com.kosta.legolego.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
     User findByUserEmail(String email);

    boolean existsByUserEmail(String UserEmail);
    boolean existsByUserNickname(String nickname);

    // 아이디 찾기
    User findByUserNameAndUserPhone(String name, String phone);

    // 비밀번호 찾기
    User findByUserEmailAndUserNameAndUserPhone(String email, String name, String phone);

}
