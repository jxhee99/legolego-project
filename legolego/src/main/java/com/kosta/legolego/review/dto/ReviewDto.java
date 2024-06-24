package com.kosta.legolego.review.dto;

import com.kosta.legolego.review.entity.Review;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewDto {
    private Long reviewNum;
    private String content;
    private Timestamp createDate;
    private Integer rating;
    private Long boardNum;

    public static ReviewDto fromEntity(Review review){
        return new ReviewDto(
                review.getReviewNum(),
                review.getContent(),
                review.getCreatedDate(),
                review.getRating(),
                review.getPreTripBoard().getBoardNum()
        );
    }

    public static Review toEntity(ReviewDto reviewDto){
        Review review = new Review();
        review.setReviewNum(reviewDto.getReviewNum());
        review.setContent(reviewDto.getContent());
        review.setCreatedDate(reviewDto.getCreateDate());
        review.setRating(reviewDto.getRating());
        return review;
    }
}
