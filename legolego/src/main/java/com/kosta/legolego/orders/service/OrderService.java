package com.kosta.legolego.orders.service;

import
        com.kosta.legolego.orders.dto.OrderDto;
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

//    새로운 주문 생성
    public OrderDto createOrder(OrderDto orderDto){
        log.info("Creating order for userNum: {} and productNum: {}",
                orderDto.getUserNum(), orderDto.getProductNum());

        User user = userRepository.findById(orderDto.getUserNum())
                .orElseThrow(()->new RuntimeException("일치하는 사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(orderDto.getProductNum())
                .orElseThrow(()-> new RuntimeException("일치하는 상품을 찾을 수 없습니다."));

        Order order = OrderDto.toEntity(orderDto); // OrdersDTO -> Orders 엔티티 변환
        order.setUser(user);
        order.setProduct(product);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with orderNum: {}", savedOrder.getOrderNum());
        return OrderDto.fromEntity(savedOrder); // 엔티티 -> DTO 변환
    }

//    관리자 모든 주문 조회
    public List<OrderDto> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }

//    사용자 주문 조회
    public List<OrderDto> getUserOrders(@RequestParam("user_num") Long userNum){
        List<Order> orders = orderRepository.findByUser_userNum(userNum);
        return orders.stream()
                .map(OrderDto::fromEntity) // 엔티티 -> Dto 변환
                .collect(Collectors.toList());
    }

//    특정 주문 조회
    public OrderDto getOrderById(Long orderNum){
        Order order = orderRepository.findById(orderNum)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderDto.fromEntity(order); // 엔티티 -> DTO 변환

    }

//    특정 주문 취소
    public void deleteOrder(Long orderNum){
        orderRepository.deleteById(orderNum);
    }

}
