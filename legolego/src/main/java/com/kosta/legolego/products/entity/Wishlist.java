package com.kosta.legolego.products.entity;

import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wishlist")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_num")
    private Long wishlist_num;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_num", nullable = false)
    private Product product;
}
