package com.kosta.legolego.admin.repository;

import com.kosta.legolego.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
//    Optional<Admin> findByAdminEmail(String adminEmail);
//    Optional<Admin> findByAdminNameAndAdminPhone(String adminName, String adminPhone);
    boolean existsById(Long adminNum);
    Admin findByAdminNum(Long adminNum);

    Admin findByAdminEmail(String adminEmail);
}
