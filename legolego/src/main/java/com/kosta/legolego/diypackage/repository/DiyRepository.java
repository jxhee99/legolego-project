package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiyRepository extends JpaRepository<DiyPackage, Long> {
  @Modifying
  @Transactional
  @Query("UPDATE DiyPackage d SET d.packageViewNum = d.packageViewNum + 1 WHERE d.packageNum = :packageNum")
  void incrementViewNum(@Param("packageNum") Long packageNum);

  //최신등록순 정렬
//  List<DiyPackage> findAllByOrderByPackageNumDesc();

  //airline 엔티티 조회
  DiyPackage findByAirlineAndPackageDraftFalse(AirlineEntity airline);

  // 임시 저장된 패키지 조회
  DiyPackage findFirstByUserUserNumAndPackageDraftTrue(Long userNum);
  DiyPackage findByUserUserNumAndPackageDraftTrue(Long userNum);

  // 최종 저장 처리된 패키지만 최신 등록 순으로 조회
  List<DiyPackage> findAllByPackageDraftFalseOrderByPackageNumDesc();

  // 인기순(좋아요) 순으로 정렬 및 같은 순위끼리는 최신 등록 순으로 정렬
  List<DiyPackage> findAllByPackageDraftFalseOrderByPackageLikedNumDescPackageNumDesc();
}
