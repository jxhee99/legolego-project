package com.kosta.legolego.review.controller;

import com.kosta.legolego.review.dto.ReviewDto;
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

    @PostMapping("/{order_num}")
    public ResponseEntity<ReviewDto> createReview(@PathVariable("order_num") Long orderNum, @RequestBody ReviewDto reviewDto){
        ReviewDto newReview = reviewService.createReview(orderNum, reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

}
