package com.kosta.legolego.admin.dto;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminLikedListDto {

    private Long packageNum;
    private User user;
    private String packageName;
    private Integer packageLikedNum;
    private Timestamp regDate;
    private Timestamp modDate;
    private Boolean packageApproval;
    private Boolean isOfferSent;
    private List<Long> listNum;

    public AdminLikedListDto(DiyPackage diyPackage) {
        this.packageNum = diyPackage.getPackageNum();
        this.packageName = diyPackage.getPackageName();
        this.packageLikedNum = diyPackage.getPackageLikedNum();
        this.packageApproval = diyPackage.getPackageApproval();
        this.listNum = diyPackage.getDiyLists().stream()
                .map(DiyList::getListNum)
                .collect(Collectors.toList());
        this.user = diyPackage.getUser();
        this.regDate = diyPackage.getRegDate();
        this.modDate = diyPackage.getModDate();
        this.isOfferSent = diyPackage.getDiyLists().stream()
                .anyMatch(DiyList::getIsOfferSent);
    }
}
