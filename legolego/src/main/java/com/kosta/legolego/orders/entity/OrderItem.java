package com.kosta.legolego.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_num")
    private Long orderItemNum;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "price",nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_num", nullable = false)
    private Orders orders;

//    @ManyToOne
//    @JoinColumn(name = "product_num",nullable = false)
//    private Product product;

}
