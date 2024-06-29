package com.kosta.legolego.review.controller;

import com.kosta.legolego.review.dto.ReviewDto;
import com.kosta.legolego.review.entity.Review;
import com.kosta.legolego.review.service.ReviewService;
import com.kosta.legolego.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    // 리뷰 생성
    @PostMapping("/user/reviews/{order_num}")
    public ResponseEntity<ReviewDto> createReview(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("order_num") Long orderNum, @RequestBody ReviewDto reviewDto){
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReviewDto newReview = reviewService.createReview(orderNum, reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    // 리뷰 수정
    @PatchMapping("/user/reviews/{review_num}/edit")
    public ResponseEntity<ReviewDto> updateReviewById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("review_num") Long reviewNum, @RequestBody ReviewDto reviewDto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Long userNum = userDetails.getId();
            ReviewDto updatedReview = reviewService.updateReview(reviewNum, reviewDto, userNum);
            return ResponseEntity.status(HttpStatus.OK).body(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/user/reviews/{review_num}/delete")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("review_num") Long reviewNum) {
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reviewService.deleteReview(reviewNum);
        return ResponseEntity.noContent().build();
    }

    // only admin - 리뷰 삭제
    @DeleteMapping("/admin/reviews/{review_num}")
    public ResponseEntity<Void> deleteReviewForAdmin(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("review_num") Long reviewNum) {
        if (userDetails == null || !userDetails.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reviewService.deleteReviewForAdmin(reviewNum);
        return ResponseEntity.noContent().build();
    }

}
