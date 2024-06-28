package com.kosta.legolego.review.controller;

import com.kosta.legolego.review.dto.ReviewDto;
import com.kosta.legolego.review.entity.Review;
import com.kosta.legolego.review.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    // 리뷰 생성
    @PostMapping("/{order_num}")
    public ResponseEntity<ReviewDto> createReview(@PathVariable("order_num") Long orderNum, @RequestBody ReviewDto reviewDto){
        ReviewDto newReview = reviewService.createReview(orderNum, reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    // 리뷰 수정
    @PatchMapping("/{review_num}/{user_num}/edit")
    public ResponseEntity<ReviewDto> updateReviewById(@PathVariable("review_num") Long reviewNum, @PathVariable("user_num") Long userNum, @RequestBody ReviewDto reviewDto) {

        try {
            ReviewDto updatedReview = reviewService.updateReview(reviewNum, reviewDto, userNum);
            return ResponseEntity.status(HttpStatus.OK).body(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/{review_num}/{user_num}/delete")
    public void deleteReview(@PathVariable("review_num") Long reviewNum, @PathVariable("user_num") Long userNum) {
            reviewService.deleteReview(reviewNum, userNum);
    }

    // only admin - 리뷰 삭제
    @DeleteMapping("/{review_num}/admin/{admin_num}")
    public void deleteReviewForAdmin(@PathVariable("review_num") Long reviewNum, @PathVariable("admin_num") Long adminNum) {
        reviewService.deleteReviewForAdmin(reviewNum, adminNum);
    }

}
