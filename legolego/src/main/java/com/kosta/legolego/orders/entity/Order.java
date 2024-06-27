package com.kosta.legolego.orders.entity;

import com.kosta.legolego.payment.entity.Payment;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.review.entity.Review;
import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_num")
    private Long orderNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_num", nullable = false)
    private User user; // user_name, user_email, user_phone 정보 조회

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_num", nullable = false)
    private Product product; // product_name, product_price 정보 조회

//    amount -> quantity 필드명 변경
    @Column(name = "quantity", nullable = false)
    private int quantity = 1; // 수량

    @Column(name = "merchant_uid", length = 100, unique = true)
    private String merchantUid; // 결제 요청 전에 생성되는 고유 주문 번호

//    주문일과 결제일 사실상 같은 것 같아서 주석처리함
//    @Column(name = "order_day")
//    private Timestamp orderDay; // 주문일

    @Column(name = "payment_status")
    private Boolean paymentStatus = false; // 결제 상태

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice; // 가격과 수량을 곱해서 사용

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToOne(fetch = FetchType.LAZY) // fetch = FetchType.LAZY : 관련 엔티티 실제로 접근할 때까지 로드되지 않음
    @JoinColumn(name = "review_num")
    private Review review; // 주문에 대한 리뷰 작성 여부 : boolean 타입으로 체크

}
