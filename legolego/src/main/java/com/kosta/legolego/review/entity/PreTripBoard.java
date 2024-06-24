package com.kosta.legolego.review.entity;

import com.kosta.legolego.products.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "pre_trip_board")
@Entity
public class PreTripBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_num")
    private Long boardNum;

    @OneToMany(mappedBy = "preTripBoard")
    private List<Review> reviews; // 특정 상품에 대한 모든 리뷰 조회 기능 구현

    @OneToOne
    @JoinColumn(name = "product_num", nullable = false)
    private Product product;
}
