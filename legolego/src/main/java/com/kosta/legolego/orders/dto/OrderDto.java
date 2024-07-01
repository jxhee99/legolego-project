package com.kosta.legolego.orders.dto;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.user.entity.User;
import lombok.*;

import java.math.BigDecimal;

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
    private BigDecimal price; // 가격
    private int quantity; // 수량
    private BigDecimal totalPrice; // 가격과 수량을 곱한 값
    private String merchantUid;
    private boolean refundStatus; // 환불 상태



    public static OrderDto fromEntity(Order order){
        return new OrderDto(
                order.getOrderNum(),
                order.getUser().getUserNum(),
                order.getUser().getUserName(),
                order.getUser().getUserEmail(),
                order.getUser().getUserPhone(),
                order.getProduct().getProductNum(),
                order.getProduct().getPrice(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getMerchantUid(),
                order.isRefundStatus()
        );
    }

    public static Order toEntity(OrderDto orderDto){
        Order order = new Order();
        order.setOrderNum(orderDto.getOrderNum());
        order.setQuantity(orderDto.getQuantity());
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setMerchantUid(orderDto.getMerchantUid());
        return order;
    }

//public static Order toEntity(OrderDto orderDto, Product product, User user){
//    Order order = new Order();
//    order.setUser(user);
//    order.setProduct(product);
//    order.setAmount(orderDto.getAmount());
//    order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(orderDto.getAmount())));
//    order.setMerchantUid(orderDto.getMerchantUid());
//    return order;
//}
}

