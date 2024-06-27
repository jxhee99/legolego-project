package com.kosta.legolego.review.dto;

import com.kosta.legolego.products.dto.ProductDetailDto;
import com.kosta.legolego.review.entity.PreTripBoard;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreTripBoardDetailDto {
    private Long boardNum;
    private ProductDetailDto productDetail;
    private List<ReviewDto> reviews;

    public static PreTripBoardDetailDto fromEntity(PreTripBoard preTripBoard, ProductDetailDto productDetail, List<ReviewDto> reviews) {
            return PreTripBoardDetailDto.builder()
                    .boardNum(preTripBoard.getBoardNum())
                    .productDetail(productDetail)
                    .reviews(reviews)
                    .build();
    }

}
