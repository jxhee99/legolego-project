package com.kosta.legolego.products.entity;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.products.dto.ProductDto;
import jakarta.persistence.*;
        import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_num", nullable = false)
    private Admin admin;

//    @OneToOne
//    @JoinColumn(name = "package_num")
//    private DiyPackage diyPackage; // package list 로 가지고 오자

    // 참조 변경
    @OneToOne
    @JoinColumn(name = "list_num", nullable = false)
    private DiyList diyList; // 가 참조하고 있는 DiyPackage가 참조하고 있는 User의 닉네임 가져오기

    // 상품 정보 필드
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_img", length = 255)
    private String productImage;

    @Column(name = "reg_date", nullable = false)
    private Timestamp regDate;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // BigDecimal : 고정 소수점 숫자를 정확하게 표현할 수 있는 클래스

    @Column(name = "recruitment_deadline", nullable = false)
    private Timestamp recruitmentDeadline;

    @Column(name = "recruitment_confirmed", nullable = false)
    private Boolean recruitmentConfirmed = false ;

    @Column(name = "product_view_num")
    private Integer productViewNum = 0;

    @Column(name = "wishlist_count")
    private int wishlistCount = 0;

//    패키지 엔티티에서 사용자와 관계가 설정되어 있기 때문에 직접 사용자와 관계 설정 x
//    패키지 엔티티를 통해서 필요한 사용자의 정보를 가져오기
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_num", nullable = false)
//    private User user;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Order> order;

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
