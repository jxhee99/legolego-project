package com.kosta.legolego.products.dto;

import com.kosta.legolego.products.entity.Product;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ProductDto { // 상세 조회 시 필요한 모든 데이터
    private Long productNum;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Date recruitmentDeadline;
    private Boolean recruitmentConfirmed;
    private Integer productViewNum;
    private Integer productLovedNum;
    private Long adminNum;
    private Long packageNum;
    private String userNickname; // 작성자 닉네임

    public static ProductDto createProductDto(Product product){
        return new ProductDto(
                product.getProductNum(),
                product.getProductName(),
                product.getProductImage(),
                product.getPrice(),
                product.getRecruitmentDeadline(),
                product.getRecruitmentConfirmed(),
                product.getProductViewNum(),
                product.getProductLovedNum(),
                product.getAdmin().getAdminNum(),
                product.getDiyPackage().getPackageNum(),
                product.getUser().getUserNickname()
        );
    }
}