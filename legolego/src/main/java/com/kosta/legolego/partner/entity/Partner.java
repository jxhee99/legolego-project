package com.kosta.legolego.partner.entity;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Partner {

    public enum PartnerStatus{
        registered,
        withdrawal
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_num")
    private Long partnerNum;

    @Column(name = "partner_email", nullable = false, unique = true, length = 100)
    private String partnerEmail;

    @Column(name = "partner_pw", nullable = false, length = 255)
    private String partnerPw;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "partner_phone", nullable = false, length = 15)
    private String partnerPhone;

    @Column(name = "profile_image", length = 1000)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_status")
    private Partner.PartnerStatus partnerStatus = Partner.PartnerStatus.registered;

}
