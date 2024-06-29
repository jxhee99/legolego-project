package com.kosta.legolego.orders.controller;

import com.kosta.legolego.orders.dto.OrderDto;
import com.kosta.legolego.orders.service.OrderService;
import com.kosta.legolego.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
//@RequestMapping("/orders")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/user/orders")
    public ResponseEntity<Map<String, String>> createOrder(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody OrderDto orderDto) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 사용자 설정
        orderDto.setUserNum(userDetails.getId());
        orderDto.setUserEmail(userDetails.getEmail());

        // 주문번호를 UUID로 생성
        String orderNumber = UUID.randomUUID().toString();
        orderDto.setMerchantUid(orderNumber);

        // 결제 시 사용자가 입력한 결제 정보를 주문 데이터로 받아 새로운 주문 생성
        OrderDto newOrder = orderService.createOrder(orderDto);

        // 주문번호, 사용자 이름, 이메일을 응답으로 반환
        Map<String, String> response = new HashMap<>();
        response.put("orderNumber", newOrder.getMerchantUid());
        response.put("userEmail", newOrder.getUserEmail());
        response.put("userName", newOrder.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // merchantUid로 주문 정보 조회
    @GetMapping("/user/merchant/{merchant_uid}")
    public ResponseEntity<OrderDto> getOrderByMerchantUid(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("merchant_uid") String merchantUid) {
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OrderDto orderDto = orderService.getOrderByMerchantUid(merchantUid);
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

    //    관리자 주문 조회
    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders(@AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null || !userDetails.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    //  사용자 주문 조회
    @GetMapping("/user/orders")
    public ResponseEntity<List<OrderDto>> getUserOrders(@AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userNum = userDetails.getId();
        List<OrderDto> orders = orderService.getUserOrders(userNum);
        return ResponseEntity.ok(orders);
//        return  ResponseEntity.status(HttpStatus.OK).body(orders);

    }

    //  사용자 주문 상세 조회
    @GetMapping("/user/orders/{order_num}")
    public ResponseEntity<OrderDto> getOrderById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("order_num") Long orderNum){
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OrderDto orderDto = orderService.getOrderById(orderNum);
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

    //  특정 주문 취소
    @DeleteMapping("/user/orders/{order_num}")
    public ResponseEntity<Void> deleteOrder(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("order_num") Long orderNum){
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        orderService.deleteOrder(orderNum);
        return ResponseEntity.noContent().build();
    }
}
