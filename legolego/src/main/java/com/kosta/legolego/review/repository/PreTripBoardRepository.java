package com.kosta.legolego.review.repository;

import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.review.entity.PreTripBoard;
import com.kosta.legolego.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreTripBoardRepository extends JpaRepository<PreTripBoard, Long> {
    PreTripBoard findByProduct(Product product);
    boolean existsByProduct(Product product);

    @Query("select p from PreTripBoard p " +
            "LEFT JOIN p.reviews r group by p.boardNum " +
            "having count(r) > 0" +
            "order by avg(r.rating) desc")
    List<PreTripBoard> findHighRating();
}
