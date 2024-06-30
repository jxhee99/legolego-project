package com.kosta.legolego.partner.repository;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartnerLikedListRepository extends JpaRepository<DiyList, Long> {
    List<DiyList> findByDiyPackage_PackageApprovalTrue();
    List<DiyList> findByPartner(Partner partner);
    List<DiyList> findByIsSelected(boolean isSelected);
    //파트너가 일치하면서, 상품등록된 리스트
    List<DiyList> findByPartnerAndIsRegisteredTrue(Partner partner);
}

