package com.kosta.legolego.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kosta.legolego.diypackage.dto.DiyAirlineDTO;
import com.kosta.legolego.diypackage.dto.DiyDetailCourseDTO;
import com.kosta.legolego.diypackage.dto.DiyRouteDTO;
import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import com.kosta.legolego.image.entity.Image;
import com.kosta.legolego.products.entity.Product;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductDetailDto {

    @JsonProperty("productNum")
    private Long productNum;

    @JsonProperty("partnerName")
    private String partnerName; // = 여행사 회사명

    @JsonProperty("productName")
    private String productName; // = packageName

    @JsonProperty("productImage")
    private String productImage; // = profileImg

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("regDate")
    private Timestamp regDate; // 등록일

    @JsonProperty("recruitmentDeadline")
    private Timestamp recruitmentDeadline;  // 모집 마감일

    @JsonProperty("necessaryPeople")
    private int necessaryPeople; // 모집 인원

    @JsonProperty("recruitmentConfirmed")
    private Boolean recruitmentConfirmed; // 모집 확정 여부

    @JsonProperty("productViewNum")
    private Integer productViewNum; // 조회수

    @JsonProperty("wishlistCount")
    private Integer wishlistCount; // 찜 개수

    @JsonProperty("userNickname")
    private String userNickname; // 작성자 닉네임

    @JsonProperty("airline")
    private DiyAirlineDTO airline; // 항공 정보

    @JsonProperty("route")
    private DiyRouteDTO route; // 여행 일정

    @JsonProperty("detailCourse")
    private List<DiyDetailCourseDTO> detailCourse; // 상세 일정



    // 파라미터가 많아 가독성이 떨어짐 -> builder() 사용하여 가독성 높임
    public static ProductDetailDto fromInfo(Product product, ProductDetailInfo info) {

        return ProductDetailDto.builder()
                .productNum(product.getProductNum())
                .partnerName(info.getDiyList().getPartner().getCompanyName())
                .productName(product.getProductName())
                .productImage(product.getProductImage())
                .price(product.getPrice())
                .regDate(product.getRegDate())
                .recruitmentDeadline(product.getRecruitmentDeadline())
                .necessaryPeople(product.getNecessaryPeople())
                .recruitmentConfirmed(product.getRecruitmentConfirmed())
                .wishlistCount(product.getWishlistCount())
                .productViewNum(product.getProductViewNum())
                .userNickname(info.getDiyList().getDiyPackage().getUser().getUserNickname())
                .airline(info.getDiyAirlineDTO())
                .route(info.getDiyRouteDTO())
                .detailCourse(info.getDiyDetailCourseDTOList())
                .build();
    }
}
