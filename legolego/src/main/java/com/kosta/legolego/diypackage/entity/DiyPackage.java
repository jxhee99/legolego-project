package com.kosta.legolego.diypackage.entity;

import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "DIY_package")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DiyPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String packageName;

    @Column(nullable = false, length = 255)
    private String profileImg;

    @Column(nullable = false)
    private Timestamp regDate;

    @Column
    private Timestamp modDate;

    @Column(nullable = false)
    private Integer packageLikedNum = 0;

    @Column(nullable = false)
    private Integer packageViewNum = 0;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String shortDescription;

//    @ManyToOne
//    @JoinColumn(name = "airline_num", nullable = false)
//    private Airline airline;
//
//    @ManyToOne
//    @JoinColumn(name = "route_num", nullable = false)
//    private Route route;
}