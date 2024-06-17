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
//    private String productName; // = packageName
    private String productImage;
    private BigDecimal price;
    private Date recruitmentDeadline; // 모집 기간
    private Boolean recruitmentConfirmed; // 모집 확정 여부
    private Integer productViewNum; // 조회수
    private Integer wishlistCount; // 찜 개수
    private Long adminNum; // 디테일에서만 보이게 조정
//    private Long packageNum;
//    private String userNickname; // 작성자 닉네임

    public static ProductDto fromEntity(Product product){
        return new ProductDto(
                product.getProductNum(),
//                product.getDiyPackage().getPackageName(),
                product.getProductImage(),
                product.getPrice(),
                product.getRecruitmentDeadline(),
                product.getRecruitmentConfirmed(),
                product.getProductViewNum(),
                product.getWishlistCount(),
                product.getAdmin().getAdminNum()
//                product.getDiyPackage().getPackageNum()
//                product.getUser().getUserNickname() // User
        );
    }

    public static Product toEntity(ProductDto productDto) {
        Product product = new Product();
        product.setProductNum(productDto.getProductNum());
//        product.setProductName(productDto.getProductName());
        product.setProductImage(productDto.getProductImage());
        product.setPrice(productDto.getPrice());
        product.setRecruitmentDeadline(productDto.getRecruitmentDeadline());
        product.setRecruitmentConfirmed(productDto.getRecruitmentConfirmed());
        product.setProductViewNum(productDto.getProductViewNum());
        product.setWishlistCount(productDto.getWishlistCount());
        return product;
    }
}