package com.kosta.legolego.products.entity;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
        import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "product")
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성을 데이터베이스에 위임
    @Column(name = "product_num")
    private Long productNum; // Spring Boot에서 Long으로 id를 선언하면서 bigint로 타입이 지정

    // DIY 패키지 필드
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_img", length = 255)
    private String productImage;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // BigDecimal : 고정 소수점 숫자를 정확하게 표현할 수 있는 클래스

    @Column(name = "recruitment_deadline", nullable = false)
    private Date recruitmentDeadline;

    @Column(name = "recruitment_confirmed", nullable = false)
    private Boolean recruitmentConfirmed = false ;

    @Column(name = "product_view_num")
    private Integer productViewNum = 0;

    @Column(name = "product_loved_num")
    private Integer productLovedNum = 0;

    @ManyToOne
    @JoinColumn(name = "admin_num", nullable = false)
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "package_num", nullable = false)
    private DiyPackage diyPackage;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

//    상품 수정 시 변경 할 수 있는 필드
    public void patch(ProductDto productDto){
        if(!this.productNum.equals(productDto.getProductNum()))
            throw new IllegalArgumentException("상품 수정 실패! 잘못된 productNum 입력");

        if(productDto.getProductName() != null)
            this.productName = productDto.getProductName();

        if(productDto.getProductImage() != null)
            this.productImage = productDto.getProductImage();

        if(productDto.getPrice() != null)
            this.price = productDto.getPrice();

        if(productDto.getRecruitmentDeadline() != null)
            this.recruitmentDeadline = productDto.getRecruitmentDeadline();

//       추가 모집 혹은 여행 확정 후 결제 취소로 인해 확정 취소 됐을 경우
        if(productDto.getRecruitmentConfirmed() != null)
            this.recruitmentConfirmed = productDto.getRecruitmentConfirmed();
    }
}
