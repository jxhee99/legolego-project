package com.kosta.legolego.diypackage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.products.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiyList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_num")
    private Long listNum;

    @Column(name = "price", nullable = true)
    private BigDecimal price;

    @Column(name = "necessary_people", nullable = true)
    private Integer necessaryPeople = 0;

    @Column(name = "special_benefits", nullable = true)
    private String specialBenefits;

    @Column(name = "reg_date", nullable = false)
    private Timestamp regDate;

    @Column(name = "mod_date", nullable = true)
    private Timestamp modDate;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected = false; // 작성자 제안 수락 여부

    @Column(name = "is_registered", nullable = false)
    private Boolean isRegistered; // 관리자 상품 등록 여부

    @Column(name = "product_num", nullable = true)
    private Long productNum;

    @ManyToOne
    @JoinColumn(name = "partner_num")
    private Partner partner;

    @ManyToOne
    @JoinColumn(name = "package_num", nullable = false)
    @JsonIgnore
    private DiyPackage diyPackage;

}
