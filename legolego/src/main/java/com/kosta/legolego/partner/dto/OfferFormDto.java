package com.kosta.legolego.partner.dto;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OfferFormDto {

    private Long packageNum;
    private BigDecimal price;
    private Integer necessaryPeople; // 필수 인원
    private String specialBenefits;
    private Long listNum;
    private Timestamp regDate;
    private Timestamp modDate;
    private User user;
    private Partner partner;

    public OfferFormDto(DiyList diyList) {
        this.packageNum = diyList.getDiyPackage().getPackageNum();
        this.price = diyList.getPrice();
        this.necessaryPeople = diyList.getNecessaryPeople();
        this.specialBenefits = diyList.getSpecialBenefits();
        this.listNum = diyList.getListNum();
        this.regDate = diyList.getRegDate();
        this.modDate = diyList.getModDate();
        this.user = diyList.getDiyPackage().getUser();
    }

    public static DiyList toEntity(OfferFormDto offerFormDto) {
        DiyList diyList = new DiyList();
        diyList.setPrice(offerFormDto.getPrice());
        diyList.setNecessaryPeople(offerFormDto.getNecessaryPeople());
        diyList.setSpecialBenefits(offerFormDto.getSpecialBenefits());
        diyList.setPartner(offerFormDto.getPartner());
        diyList.setIsRegistered(false);
        DiyPackage diyPackage = new DiyPackage();
        diyPackage.setPackageNum(offerFormDto.getPackageNum());
        diyList.setDiyPackage(diyPackage);

        return diyList;
    }
}
