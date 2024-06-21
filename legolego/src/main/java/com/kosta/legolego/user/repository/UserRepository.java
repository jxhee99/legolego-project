package com.kosta.legolego.user.repository;

import com.kosta.legolego.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserNameAndUserPhone(String userName, String userPhone);
}
