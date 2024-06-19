package com.kosta.legolego.diypackage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Entity
public class OverLikedList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "over_liked_list_num")
    private Long overLikedListNum;

    @OneToOne
    @JoinColumn(name = "package_num")
    private DiyPackage diyPackage;

}
