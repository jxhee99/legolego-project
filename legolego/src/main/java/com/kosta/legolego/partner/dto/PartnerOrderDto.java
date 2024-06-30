package com.kosta.legolego.partner.dto;

import com.kosta.legolego.orders.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class PartnerOrderDto {
  private Long orderNum;
  private String userName;
  private String userEmail;
  private String userPhone;
  private Long productNum;
  private BigDecimal price; // 가격
  private int quantity; // 수량
  private BigDecimal totalPrice; // 가격과 수량을 곱한 값

  public static PartnerOrderDto toDto(Order order) {
    return new PartnerOrderDto(
            order.getOrderNum(),
            order.getUser().getUserName(),
            order.getUser().getUserEmail(),
            order.getUser().getUserPhone(),
            order.getProduct().getProductNum(),
            order.getProduct().getPrice(),
            order.getQuantity(),
            order.getTotalPrice()
    );
  }
  public static List<PartnerOrderDto> toDtoList(List<Order> orderList) {
    List<PartnerOrderDto> orderDtoList = new ArrayList<>();
    for (Order order : orderList) {
      orderDtoList.add(toDto(order));
    }
    return orderDtoList;
  }
}
