package com.kosta.legolego.products.dto;

import com.kosta.legolego.diypackage.dto.DiyAirlineDTO;
import com.kosta.legolego.diypackage.dto.DiyDetailCourseDTO;
import com.kosta.legolego.diypackage.dto.DiyRouteDTO;
import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.entity.RouteEntity;
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

    private Long productNum;
    private String partnerName; // = 여행사 회사명
    private String productName; // = packageName
    private String productImage; // = profileImg
    private BigDecimal price;
    private Timestamp regDate; // 등록일
    private Timestamp recruitmentDeadline;  // 모집 마감일
    private int necessaryPeople; // 모집 인원
    private Boolean recruitmentConfirmed; // 모집 확정 여부
    private Integer productViewNum; // 조회수
    private Integer wishlistCount; // 찜 개수
    private String userNickname; // 작성자 닉네임
    private DiyAirlineDTO airline; // 항공 정보
    private DiyRouteDTO route; // 여행 일정
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
