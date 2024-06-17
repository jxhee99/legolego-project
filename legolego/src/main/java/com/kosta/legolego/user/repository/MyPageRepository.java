package com.kosta.legolego.user.repository;

import com.kosta.legolego.diypackage.entity.DiyList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyPageRepository extends JpaRepository<DiyList, Long> {

    // 내가 쓴 글 리스트 조회
//    List<MyPage> findByUserUserNum(Long userNum);

    // 내가 쓴 글 목록 중 응원하기 조건 달성한 리스트만 조회
//    List<DiyList> findByUserUserNumAndDiyListDiyPackagePackageLikedNumGreaterThanEqual(Long userNum, Integer packageLikedNum);

    List<DiyList> findByDiyPackageUserUserNumAndDiyPackagePackageLikedNumGreaterThanEqual(Long userNum, Integer packageLikedNum);


}
