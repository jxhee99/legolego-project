package com.kosta.legolego.admin.repository;

import com.kosta.legolego.diypackage.entity.DiyList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminLikedListRepository extends JpaRepository<DiyList, Long> {

    // 응원하기 조건 충족한 DIY 패키지 리스트 조회
    List<DiyList> findAllByDiyPackage_PackageLikedNumGreaterThanEqual(Integer packageLikedNum);
//    List<LikedList> findAllByStatus(Integer status);

    // DIY 패키지 승인 여부
//    Admin isPackageApproval(boolean packageApproval);
}
