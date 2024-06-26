package com.kosta.legolego.products.repository;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
//    검색 기능 쿼리 메서드 생성
   List<Product> findByProductNameContaining(String keyword);

   // 지난여행 게시판 이동 위한 메서드
   @Query("select p from Product p " +
           "where p.recruitmentConfirmed = true " +
           "and p.diyList.diyPackage.airline.boardingDate < :currentTimestamp")
   List<Product> findByConfirmedAndBoardingDateBefore(@Param("currentTimestamp") LocalDateTime currentTimestamp);

   // 상품의 모집 확정 여부를 검사하기 위한 메서드
   @Query("select p from Product p where p.recruitmentConfirmed = false")
   List<Product> findUnRecruitmentConfirmedProducts();

   // 자동 환불 처리 위한 메서드
   @Query("select p from Product p " +
           "where p.recruitmentConfirmed = false " +
           "and p.recruitmentDeadline < :currentTimestamp")
   List<Product> findUnConfirmedProductPastDeadlineBefore(@Param("currentTimestamp") LocalDateTime currentTimestamp);
}
