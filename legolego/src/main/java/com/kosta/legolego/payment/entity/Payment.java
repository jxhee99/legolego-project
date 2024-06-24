package com.kosta.legolego.payment.entity;

import com.kosta.legolego.orders.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_num")
    private Long paymentNum;

    @Column(name = "payment_date", nullable = false)
    private Timestamp paymentDate;

    @Column(name = "imp_uid")
    private String impUid;

    @Column(name = "merchant_uid", nullable = false)
    private  String merchantUid; // 주문 테이블에서 가져오는 주문 고유 번호

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 총 가격

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // 결제 방법

    @Column(name = "status", length = 20)
    private String status; // 결제 상태

    @ManyToOne
    @JoinColumn(name = "order_num")
    private Order order;

//    @ManyToOne
//    @JoinColumn(name = "product_num", nullable = false)
//    private Product product;
}