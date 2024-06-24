package com.kosta.legolego.review.service;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.review.dto.ReviewDto;
import com.kosta.legolego.review.entity.Review;
import com.kosta.legolego.review.repository.PreTripBoardRepository;
import com.kosta.legolego.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PreTripBoardRepository preTripBoardRepository;

    // 리뷰 생성
    @Transactional
    public ReviewDto createReview(Long orderNum, ReviewDto reviewDto){
        Order order = orderRepository.findById(orderNum)
                .orElseThrow(() -> new RuntimeException("주문번호를 찾을 수 없습니다."));

        // 주문한 상품이 지난 여행 게시판에 있는지 확인
        if(preTripBoardRepository.existsByProduct(order.getProduct())){
            Review review = ReviewDto.toEntity(reviewDto);
            review.setOrder(order);

            review.setPreTripBoard(preTripBoardRepository.findByProduct(order.getProduct()));
            Review savedReview = reviewRepository.save(review);
            return ReviewDto.fromEntity(savedReview);
        } else {
            throw new IllegalArgumentException("상품에 대한 리뷰를 작성할 수 없습니다.");
        }

    }

    // 리뷰 수정

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewNum){
        Review review = reviewRepository.findById(reviewNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));
        reviewRepository.delete(review);

    }
}
