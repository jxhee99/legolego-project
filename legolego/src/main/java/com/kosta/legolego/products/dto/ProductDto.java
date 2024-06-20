package com.kosta.legolego.products.dto;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.products.entity.Product;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ProductDto { // 조회 시 필요한 모든 데이터

    private Long productNum;
    private Long listNum;
    private String productName; // = packageName
    private String productImage; // = diypackage img 필드
    private BigDecimal price; // list에서 가져오기
    private Timestamp regDate; // 등록일
    private Timestamp recruitmentDeadline;  // 모집 마감일
    private Boolean recruitmentConfirmed; // 모집 확정 여부
    private Integer productViewNum; // 조회수
    private Integer wishlistCount; // 찜 개수
//    private Long adminNum; // 디테일에서만 보이게 조정
    private String userNickname; // 작성자 닉네임

    public static ProductDto fromEntity(Product product){
        return new ProductDto(
                product.getProductNum(),
                product.getDiyList().getListNum(),
                product.getDiyList().getDiyPackage().getPackageName(),
                product.getDiyList().getDiyPackage().getProfileImg(),
                product.getDiyList().getPrice(),
                product.getRegDate(),
                product.getRecruitmentDeadline(),
                product.getRecruitmentConfirmed(),
                product.getProductViewNum(),
                product.getWishlistCount(),
//                product.getAdmin().getAdminNum(),
                product.getDiyList().getDiyPackage().getUser().getUserNickname() // User
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