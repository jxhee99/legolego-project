package com.kosta.legolego.products.repository;

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

   @Query("select p from Product p " +
           "where p.recruitmentConfirmed = true " +
           "and p.diyList.diyPackage.airline.boardingDate < :currentTimestamp")
   List<Product> findByConfirmedAndBoardingDateBefore(@Param("currentTimestamp") LocalDateTime currentTimestamp);
}
