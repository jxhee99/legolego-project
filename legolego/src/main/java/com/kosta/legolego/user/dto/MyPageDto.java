package com.kosta.legolego.user.dto;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import com.kosta.legolego.user.entity.User;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDto {

    private Long packageNum;
    private String packageName;
    private String profileImg;
    private LocalDate regDate;
    private LocalDate modDate;
    private int packageLikedNum = 0;
    private int packageViewNum = 0;
    private String shortDescription;
    private User user;
    private AirlineEntity airline;
    private RouteEntity route;


    public MyPageDto(DiyPackage diyPackage) {
        this.packageNum = diyPackage.getPackageNum();
        this.packageName = diyPackage.getPackageName();
        this.profileImg = diyPackage.getProfileImg();
        this.regDate = diyPackage.getRegDate();
        this.modDate = diyPackage.getModDate();
        this.packageLikedNum = diyPackage.getPackageLikedNum();
        this.packageViewNum = diyPackage.getPackageViewNum();
        this.shortDescription = diyPackage.getShortDescription();
        this.user = diyPackage.getUser();
        this.airline = diyPackage.getAirline();
        this.route = diyPackage.getRoute();
    }

}
