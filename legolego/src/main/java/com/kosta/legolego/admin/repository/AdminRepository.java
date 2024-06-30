package com.kosta.legolego.admin.repository;

import com.kosta.legolego.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsById(Long adminNum);
    Admin findByAdminNum(Long adminNum);

    boolean existsByAdminEmail(String adminEmail);

    // 아이디 찾기
    Admin findByAdminEmail(String adminEmail);

    // 비밀번호 찾기
//    Admin findByAdminEmailAndAdminNameAndAdminPhone(String email, String name, String phone);
}
