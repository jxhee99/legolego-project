package com.kosta.legolego.user.dto;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.user.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDto {

    private User user;
    private Long listNum;
    private String packageName;
    private Integer packageLikedNum;
//    private DiyPackage diyPackage;

    public MyPageDto(DiyList diyList) {
        this.listNum = diyList.getListNum();
//        this.diyPackage = myPage.getDiyPackage().getPackageNum();
        this.user = diyList.getDiyPackage().getUser();
        this.packageName = diyList.getDiyPackage().getPackageName();
        this.packageLikedNum = diyList.getDiyPackage().getPackageLikedNum();
    }
}
