package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiyListRepository extends JpaRepository<DiyList, Long> {

    // 관리자용 조회 : over_liked_list 에 존재하는 모든 패키지
    List<DiyList> findAll();

    // 여행사용 조회 : 특정 여행사가 제안한 모든 패키지
    List<DiyList> findAllByPartner_partnerNum(Long partnerNum);

    // 사용자용 조회 : 특정 사용자가 작성한 over_liked_list에 있는 모든 패키지
    List<DiyList> findAllByDiyPackage_User_userNum(Long userNum);

    // 선택되지 않은 다른 제안 패키지 조회
    List<DiyList> findAllByDiyPackage_packageNumAndIsSelected_Null(Long packageNum);

}
