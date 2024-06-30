package com.kosta.legolego.partner.dto;

import com.kosta.legolego.products.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
@Builder
@Data
public class PartnerProductDto {
  private Long productNum;
  private String productName;
  private int necessaryPeople;
  private BigDecimal price;
  private Boolean recruitmentConfirmed; // 모집 확정 여부
  private Timestamp recruitmentDeadline; // 모집 기간
  private List<PartnerOrderDto> orders; // Order의리스트

  // Lombok @Builder를 사용하여 빌더 패턴으로 객체 생성
  public static PartnerProductDto toDto(Product product, List<PartnerOrderDto> orderList) {
    return PartnerProductDto.builder()
            .productNum(product.getProductNum())
            .productName(product.getProductName())
            .necessaryPeople(product.getNecessaryPeople())
            .price(product.getPrice())
            .recruitmentConfirmed(product.getRecruitmentConfirmed())
            .recruitmentDeadline(product.getRecruitmentDeadline())
            .orders(orderList)
            .build();
  }
}