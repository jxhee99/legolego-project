package com.kosta.legolego.partner.repository;

import com.kosta.legolego.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
