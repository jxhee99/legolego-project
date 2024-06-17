package com.kosta.legolego.diypackage.entity;

import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


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


    @Column(nullable = true)
    private Timestamp modDate;

    @Column(name = "package_liked_num", nullable = true)
    private Integer packageLikedNum = 0;

    @Column(name = "package_view_num", nullable = true)
    private Integer packageViewNum = 0;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "package_approval", nullable = false)
    private Boolean packageApproval = false;

    @OneToMany(mappedBy = "diyPackage", cascade = CascadeType.ALL)
    private List<DiyList> diyLists = new ArrayList<>();


//    @ManyToOne
//    @JoinColumn(name = "airline_num", nullable = false)
//    private Airline airline;
//
//    @ManyToOne
//    @JoinColumn(name = "route_num", nullable = false)
//    private Route route;
}