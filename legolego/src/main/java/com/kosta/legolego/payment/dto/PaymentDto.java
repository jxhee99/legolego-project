package com.kosta.legolego.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class PaymentDto {
    private Long paymentId;
    private Date paymentDate;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private Long productNum;
}
