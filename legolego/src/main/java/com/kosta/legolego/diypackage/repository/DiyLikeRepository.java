package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.entity.DiyLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiyLikeRepository extends JpaRepository<DiyLikeEntity, Long> {
  DiyLikeEntity findByUserNumAndDiy(Long userNum, DiyPackage diy);
  //사용자가 diy 패키지 응원하기에 참여 했는 지 확인하는 메서드
  boolean existsByUserNumAndDiy(Long userNum, DiyPackage diy);

  List<DiyLikeEntity> findByDiy(DiyPackage diy);

  // 사용자가 응원하기 버튼 누른 게시물 리스트 확인
  List<DiyLikeEntity> findByUserNum(Long userNum);
}
