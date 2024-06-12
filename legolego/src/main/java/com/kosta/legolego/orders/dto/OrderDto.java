package com.kosta.legolego.orders.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderNum;
    private Date orderDate;
    private String status;
    private BigDecimal totalPrice;
    private Long userNum;
    private Long paymentNum;
    private Long reviewNum;
}