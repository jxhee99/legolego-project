package com.kosta.legolego.review.dto;

import com.kosta.legolego.review.entity.PreTripBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreTripBoardDto {
    private Long boardNum;
    private String productImage;
    private String productName;
    private BigDecimal price;
    private LocalDateTime boardingDate;
    private LocalDateTime comingDate;
    private String userNickname;
    private String destination;
    private Integer countRating;
    private Double avgRating;

    public static PreTripBoardDto fromEntity(PreTripBoard preTripBoard) {
        return new PreTripBoardDto(
                preTripBoard.getBoardNum(),
                preTripBoard.getProduct().getProductImage(),
                preTripBoard.getProduct().getProductName(),
                preTripBoard.getProduct().getPrice(),
                preTripBoard.getProduct().getDiyList().getDiyPackage().getAirline().getBoardingDate(),
                preTripBoard.getProduct().getDiyList().getDiyPackage().getAirline().getComingDate(),
                preTripBoard.getProduct().getDiyList().getDiyPackage().getUser().getUserNickname(),
                preTripBoard.getProduct().getDiyList().getDiyPackage().getAirline().getDestination(),
                preTripBoard.getReviews().size(),
                preTripBoard.getAverageRating()
        );
    }
}
