package com.kosta.legolego.user.repository;

import com.kosta.legolego.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository2 extends JpaRepository<User, Long> {
}