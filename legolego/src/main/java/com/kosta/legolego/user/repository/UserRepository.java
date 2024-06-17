package com.kosta.legolego.user.repository;

import com.kosta.legolego.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface UserRepository extends JpaRepository<SiteUser, String> {
    Optional<SiteUser> findByUserEmail(String userEmail);

}
