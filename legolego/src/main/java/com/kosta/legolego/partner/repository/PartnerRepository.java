package com.kosta.legolego.partner.repository;

import com.kosta.legolego.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    boolean existsByPartnerEmail(String partnerEmail);

    Partner findByPartnerEmail(String partnerEmail);

    // 아이디 찾기
    Partner findByCompanyNameAndPartnerPhone(String companyName, String phone);

    // 비밀번호 찾기
    Partner findByPartnerEmailAndCompanyNameAndPartnerPhone(String email, String name, String phone);
}

