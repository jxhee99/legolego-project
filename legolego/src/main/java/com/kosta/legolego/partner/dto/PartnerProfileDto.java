package com.kosta.legolego.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartnerProfileDto {

    private Long partnerNum;
    private String partnerEmail;
    private String companyName;
    private String partnerPhone;

}
