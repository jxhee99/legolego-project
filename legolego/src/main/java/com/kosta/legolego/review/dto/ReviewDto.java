package com.kosta.legolego.review.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewDto {
    private Long reviewNum;
    private Long content;
    private Date createDate;
    private Integer rating;
    private Long boardNum;
}
