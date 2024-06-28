package com.kosta.legolego.review.repository;

import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.review.entity.PreTripBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreTripBoardRepository extends JpaRepository<PreTripBoard, Long> {
    PreTripBoard findByProduct(Product product);
    boolean existsByProduct(Product product);
//    Long findByBoardNum(Long borad)
}
