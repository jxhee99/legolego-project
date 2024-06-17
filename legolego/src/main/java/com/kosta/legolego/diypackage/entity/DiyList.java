package com.kosta.legolego.diypackage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "is_offer_sent", nullable = false)
    private Boolean isOfferSent = false;

    @ManyToOne
    @JoinColumn(name = "package_num", nullable = false)
    @JsonIgnore
    private DiyPackage diyPackage;
}
