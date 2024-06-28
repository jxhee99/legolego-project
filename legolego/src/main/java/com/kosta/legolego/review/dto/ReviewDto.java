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
    private Long userNum;
    private String userNickname;

    public static ReviewDto fromEntity(Review review){
        Long userNum = null;
        String userNickname = null;
        if (review.getOrder() != null && review.getOrder().getUser() != null) {
            userNum = review.getOrder().getUser().getUserNum();
            userNickname = review.getOrder().getUser().getUserNickname();
        }

        return new ReviewDto(
                review.getReviewNum(),
                review.getContent(),
                review.getCreatedDate(),
                review.getRating(),
                review.getPreTripBoard() != null ? review.getPreTripBoard().getBoardNum() : null,
                userNum,
                userNickname
        );
    }

    public static Review toEntity(ReviewDto reviewDto){
        Review review = new Review();
        review.setReviewNum(reviewDto.getReviewNum());
        review.setContent(reviewDto.getContent());
        review.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        review.setRating(reviewDto.getRating());
        return review;
    }
}
