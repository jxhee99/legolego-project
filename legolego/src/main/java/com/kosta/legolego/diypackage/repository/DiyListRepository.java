package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface DiyListRepository extends JpaRepository<DiyList, Long> {

    // 관리자용 조회 : over_liked_list 에 존재하는 모든 패키지
    List<DiyList> findAll();
    //최신순으로 반환
    List<DiyList> findAllByOrderByListNumDesc();

    // 여행사용 조회 : 특정 여행사가 제안한 모든 패키지
    List<DiyList> findAllByPartner_partnerNum(Long partnerNum);

    // 여행사용 조회 : 특정 여행사가 제안한 모든 패키지를 ListNum 기준으로 내림차순 정렬
    List<DiyList> findAllByPartner_partnerNumOrderByListNumDesc(Long partnerNum);

    // 사용자용 조회 : 특정 사용자가 작성한 over_liked_list에 있는 모든 패키지
    List<DiyList> findAllByDiyPackage_User_userNum(Long userNum);

    // 선택되지 않은 다른 제안 패키지 조회
    List<DiyList> findAllByDiyPackage_packageNumAndIsSelected_Null(Long packageNum);

    Optional<DiyList> findByProductNum(Long productNum);

    //추천 상품을 위한 메서드
    List<DiyList> findByDiyPackage(DiyPackage diyPackage);

    //여행기간 지난 용: isRegistered가 false인 리스트의 DiyPackage 반환
    @Query("SELECT dl.diyPackage FROM DiyList dl WHERE dl.isRegistered = false")
    List<DiyPackage> findDiyPackagesNotRegistered();

    //여행기간 지난 용 : 해당 DiyPackage 참조 하는 리스트 삭제
    void deleteByDiyPackage(DiyPackage diyPackage);
}
