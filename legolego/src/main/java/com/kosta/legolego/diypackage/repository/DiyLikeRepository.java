package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.entity.DiyLikeEntity;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DiyLikeRepository extends JpaRepository<DiyLikeEntity, Long> {
  DiyLikeEntity findByUserNumAndDiy(Long userNum, DiyEntity diy);
  //사용자가 diy 패키지 응원하기에 참여 했는 지 확인하는 메서드
  boolean existsByUserNumAndDiy(Long userNum, DiyEntity diy);
}
