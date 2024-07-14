package com.kosta.legolego.products.repository;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductSearchRepository extends JpaRepository<Product, Long> {

  //목적지 검색을 위한 메서드
  @Query("SELECT p FROM Product p WHERE p.destination LIKE %:destination% AND p.recruitmentDeadline > :now ORDER BY p.regDate DESC")
  List<Product> findBYDestinationProducts(@Param("destination") String destination, @Param("now") LocalDateTime now);

  //월별 검색을 위한 메서드
  @Query("SELECT p FROM Product  p WHERE FUNCTION('MONTH', p.recruitmentDeadline) = :month AND p.recruitmentDeadline > :now ORDER BY p.regDate DESC" )
  List<Product> findByMonthProducts(@Param("month") int month, @Param("now") LocalDateTime now);
  //통합 검색을 위한 메서드
  @Query("SELECT p FROM Product p WHERE p.destination LIKE %:destination% AND FUNCTION('MONTH', p.recruitmentDeadline) = :month AND p.recruitmentDeadline > :now ORDER BY p.regDate DESC")
  List<Product> findByDestinationAndMonth(@Param("destination") String destination,@Param("month") int month, @Param("now") LocalDateTime now);

  //Diy패키지에서 상품 추천을 위한 메서드
  Product findByDiyList(DiyList diyList);
}
