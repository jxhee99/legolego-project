package com.kosta.legolego.user.repository;

import com.kosta.legolego.diypackage.entity.DiyPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyPageRepository extends JpaRepository<DiyPackage, Long> {

    // 내가 쓴 글 리스트 조회
    List<DiyPackage> findByUserUserNum(Long userNum);

    // 내가 쓴 글 목록 중 응원하기 조건 달성한 리스트만 조회
//    List<DiyList> findByDiyPackageUserUserNumAndDiyPackagePackageLikedNumGreaterThanEqual(Long userNum, Integer packageLikedNum);


}
