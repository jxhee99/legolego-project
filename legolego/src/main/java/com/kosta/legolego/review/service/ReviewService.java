package com.kosta.legolego.review.service;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.review.dto.ReviewDto;
import com.kosta.legolego.review.entity.Review;
import com.kosta.legolego.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

//    @Autowired
//    ReviewRepository reviewRepository;
//
//    @Autowired
//    OrderRepository orderRepository;
//
//    public ReviewDto createReview(Long orderNum, ReviewDto reviewDto){
//        Order order = orderRepository.findById(orderNum)
//                .orElseThrow(() -> new RuntimeException("주문번호를 찾을 수 없습니다."));
//
////        Review review = ReviewDto.toEntity(reviewDto);
////        review.setOrder(order); // 주문번호를 찾아서 리뷰와 연관시킴
////
////        Review savedReview = reviewRepository.save(review);
////        return ReviewDto.fromEntity(savedReview);
//    }
}
