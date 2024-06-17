package com.kosta.legolego.orders.dto;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.payment.entity.Payment;
import com.kosta.legolego.review.entity.Review;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long orderNum;
    private Long userNum;
    private String userName;
    private String userEmail;
    private String userPhone;
    private Long productNum;
    private String productName;
    private BigDecimal price; // 가격
    private int amount; // 수량
    private String merchantUid; // 포트원 제공 고유 주문 번호
    private Timestamp orderDay; // 주문일
    private Boolean paymentStatus; // 결제 상태
    private BigDecimal totalPrice; // 가격과 수량을 곱한 값
    private List<Payment> payments;
    private Review review;


//  order 엔티티 -> OrderDto 변환
    public static OrderDto fromEntity(Order order){
        return new OrderDto(
                order.getOrderNum(),
                order.getUser().getUserNum(),
                order.getUser().getUserName(),
                order.getUser().getUserEmail(),
                order.getUser().getUserPhone(),
                order.getProduct().getProductNum(),
                order.getProduct().getProductName(),
                order.getProduct().getPrice(),
                order.getAmount(),
                order.getMerchantUid(),
                order.getOrderDay(),
                order.getPaymentStatus(),
                order.getTotalPrice(),
                order.getPayments(),
                order.getReview()
        );

    }

//    OrderDto -> Order 엔티티로 변환
    public static Order toEntity(OrderDto orderDto){
        Order order = new Order();
        order.setOrderNum(orderDto.getOrderNum());
        order.setAmount(orderDto.getAmount());
        order.setMerchantUid(orderDto.getMerchantUid());
        order.setOrderDay(orderDto.getOrderDay());
        order.setPaymentStatus(orderDto.getPaymentStatus());
        order.setTotalPrice(orderDto.getTotalPrice());
        return order;
    }
}
