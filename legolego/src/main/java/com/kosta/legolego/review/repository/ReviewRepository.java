package com.kosta.legolego.review.repository;

import com.kosta.legolego.review.entity.PreTripBoard;
import com.kosta.legolego.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPreTripBoard(PreTripBoard preTripBoard);

}
