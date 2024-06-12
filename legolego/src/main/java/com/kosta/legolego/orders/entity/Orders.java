package com.kosta.legolego.orders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_num")
    private Long orderNum;

    @Column(name = "orders_date", nullable = false)
    private Date orderDate;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

//    @ManyToOne
//    @JoinColumn(name = "user_num", nullable = false)
//    private User user;
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<OrderItem> orderItems;
//
//    @ManyToOne
//    @JoinColumn(name = "payment_num", nullable = false)
//    private Payment payment;
//
//    @OneToOne
//    @JoinColumn(name = "review_num")
//    private Review review;

}
