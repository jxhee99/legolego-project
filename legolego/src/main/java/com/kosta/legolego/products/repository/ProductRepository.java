package com.kosta.legolego.products.repository;

import com.kosta.legolego.diypackage.entity.DiyList;
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
           "and p.reviewAble = false " +
           "and p.diyList.diyPackage.airline.boardingDate < :currentTimestamp")
   List<Product> findByConfirmedAndReviewUnableAndBoardingDateBefore(@Param("currentTimestamp") LocalDateTime currentTimestamp);

   // 상품의 모집 확정 여부를 검사하기 위한 메서드
   @Query("select p from Product p where p.recruitmentConfirmed = false")
   List<Product> findUnRecruitmentConfirmedProducts();

   // 자동 환불 처리 위한 메서드
   @Query("select p from Product p " +
           "where p.recruitmentConfirmed = false " +
           "and p.recruitmentDeadline < :currentTimestamp")
   List<Product> findUnConfirmedProductPastDeadlineBefore(@Param("currentTimestamp") LocalDateTime currentTimestamp);

   //추천 상품을 위한 메서드
   @Query("SELECT p FROM Product p WHERE p.destination = :destination AND p.productNum <> :productNum AND p.recruitmentDeadline > :now")
   List<Product> findRecommendedProducts(@Param("destination") String destination, @Param("productNum") Long productNum, @Param("now") LocalDateTime now);

   // 모집 기간 지난 상품 처리 메서드
   @Query("select p from Product p " +
           "where p.diyList.diyPackage.airline.boardingDate > :currentTimestamp")
   List<Product> findBeforeBoardingDate(@Param("currentTimestamp") LocalDateTime currentTimestamp);

   // 모집 확정된 상품 필터링
   @Query("select p from Product p where p.recruitmentConfirmed = true")
   List<Product> findRecruitmentConfirmed();

   // 마감 임박 상품 필터링(모집 확정 제외)
   @Query("select p from Product p " +
           "where p.recruitmentConfirmed = false " +
           "order by p.recruitmentDeadline asc")
   List<Product> findRecruitmentDeadlineAsc();

   // 최신 등록 상품 필터링
   @Query("select p from Product p order by p.regDate desc")
   List<Product> findLatestProducts();

   // 가격 높은 순
   @Query("select p from Product p order by p.price desc")
   List<Product> findPriceDesc();

   // 가격 낮은 순
   @Query("select p from Product p order by p.price asc")
   List<Product> findPriceAsc();

   // 인기순 필터링(모집 확정 제외)
   @Query("select p from Product p " +
           "where p.recruitmentConfirmed = false " +
           "order by (select count(o) from Order o where o.product = p and o.paymentStatus = true) desc ")
   List<Product> findPopularProducts();

}
