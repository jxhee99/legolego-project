package com.kosta.legolego.products.dto;

import com.kosta.legolego.products.entity.Product;

import java.math.BigDecimal;
import java.util.Date;

public class ProductDetailDto { // 항공+코스 등 패키지 상세 정보 + 여행사 제안 가격
//    private Long productNum;
//    private String productName;
//    private String productImage;
//    private BigDecimal price;
//    private Date recruitmentDeadline; // 모집 기간
//    private Boolean recruitmentConfirmed; // 모집 확정 여부
//    private Integer productViewNum; // 조회수
//    private Integer wishlistCount; // 찜 개수
//    private Long adminNum; //
    private String userNickname;


    public ProductDetailDto(Product product) {
        this.userNickname = product.getDiyPackage().getUser().getUserNickname();
    }
}
