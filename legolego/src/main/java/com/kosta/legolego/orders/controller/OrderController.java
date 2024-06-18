package com.kosta.legolego.orders.controller;

import com.kosta.legolego.orders.dto.OrderDto;
import com.kosta.legolego.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/orders")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderDto orderDto) {
        // 주문번호를 UUID로 생성
        String orderNumber = UUID.randomUUID().toString();
        orderDto.setMerchantUid(orderNumber);

        // 결제 시 사용자가 입력한 결제 정보를 주문 데이터로 받아 새로운 주문 생성
        OrderDto newOrder = orderService.createOrder(orderDto);

        // 주문번호를 응답으로 반환
        Map<String, String> response = new HashMap<>();
        response.put("orderNumber", newOrder.getMerchantUid());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // merchantUid로 주문 정보 조회
    @GetMapping("/merchant/{merchant_uid}")
    public ResponseEntity<OrderDto> getOrderByMerchantUid(@PathVariable("merchant_uid") String merchantUid) {
        OrderDto orderDto = orderService.getOrderByMerchantUid(merchantUid);
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

//    관리자 주문 조회
    @GetMapping("/admin")
    public ResponseEntity<List<OrderDto>> getAllOrders(){
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

//  사용자 주문 조회
    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(@RequestParam("user_num") Long userNum){
        List<OrderDto> orders = orderService.getUserOrders(userNum);
        return ResponseEntity.ok(orders);
//        return  ResponseEntity.status(HttpStatus.OK).body(orders);

    }

//  사용자 주문 상세 조회
    @GetMapping("/{order_num}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("order_num") Long orderNum){
        OrderDto orderDto = orderService.getOrderById(orderNum);
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

//  특정 주문 취소
    @DeleteMapping("/{order_num}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("order_num") Long orderNum){
        orderService.deleteOrder(orderNum);
        return ResponseEntity.noContent().build();
    }
}
