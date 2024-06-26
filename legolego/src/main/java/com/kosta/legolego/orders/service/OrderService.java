package com.kosta.legolego.orders.service;

import com.kosta.legolego.orders.dto.OrderDto;
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.payment.service.PaymentService;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @Autowired
    PaymentService paymentService;

    // 새로운 주문 정보 생성
    @Transactional
    public OrderDto createOrder(OrderDto orderDto){
        log.info("Creating order for userNum: {} and productNum: {}",
                orderDto.getUserNum(), orderDto.getProductNum());

        // 사용자 조회
        User user = userRepository.findById(orderDto.getUserNum())
                .orElseThrow(()->new RuntimeException("일치하는 사용자를 찾을 수 없습니다."));
        // 상품 조회
        Product product = productRepository.findById(orderDto.getProductNum())
                .orElseThrow(()-> new RuntimeException("일치하는 상품을 찾을 수 없습니다."));

        Order order = OrderDto.toEntity(orderDto);
        order.setUser(user);
        order.setProduct(product);
        order.setTotalPrice(orderDto.getTotalPrice());


        // 주문 정보 저장
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with orderNum: {}", savedOrder.getOrderNum());

        return OrderDto.fromEntity(savedOrder); // 엔티티 -> DTO 변환
    }


    // 모집 확정 메서드 (necessaryPeople 보다 많을 경우)
    @Transactional
    private void updateProductRecruitmentStatus(Product product){
        long paymentCount = orderRepository.countByProductAndPaymentStatus(product, true);
        log.info("결제 인원 수 : {}", paymentCount);
        log.info("모집 인원 수 : {}", product.getNecessaryPeople());

        if(paymentCount >= product.getNecessaryPeople() ) {
            product.setRecruitmentConfirmed(true);
            productRepository.save(product);
        }
    }

    // 배치 작업을 주기적으로 실행하는 스케줄러
    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void updateRecruitmentStatus() {
        List<Product> products = productRepository.findUnRecruitmentConfirmedProducts();
        for (Product product : products) {
            updateProductRecruitmentStatus(product);
        }
    }

    // merchantUid로 주문 정보 조회
    public OrderDto getOrderByMerchantUid(String merchantUid) {
        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new RuntimeException("주문번호를 찾을 수 없습니다."));
        return OrderDto.fromEntity(order);
    }

    // merchantUid로 주문 엔티티 조회 : 결제 처리 로직을 위해 엔티티를 직접 다루기
    public Order getOrderEntityByMerchantUid(String merchantUid) {
        return  orderRepository.findByMerchantUid(merchantUid).orElseThrow(()-> new RuntimeException("주문번호를 찾을 수 없습니다."));
    }


    // orderNum으로 특정 주문 조회
    public OrderDto getOrderById(Long orderNum){
        Order order = orderRepository.findById(orderNum)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderDto.fromEntity(order); // 엔티티 -> DTO 변환

    }

    // 관리자 모든 주문 조회
    public List<OrderDto> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 사용자 주문 조회
    public List<OrderDto> getUserOrders(@RequestParam("user_num") Long userNum){
        List<Order> orders = orderRepository.findByUser_userNum(userNum);
        return orders.stream()
                .map(OrderDto::fromEntity) // 엔티티 -> Dto 변환
                .collect(Collectors.toList());
    }


    // 특정 주문 취소
    @Transactional
    public void deleteOrder(Long orderNum){

        Order order = orderRepository.findById(orderNum)
                .orElseThrow(()-> new RuntimeException("주문을 찾을 수 없습니다."));

        Product product = order.getProduct();
        if(product == null) {
            throw new RuntimeException("주문에 해당하는 상품을 찾을 수 없습니다.");
        }

        try {
            // 현재 시간 LocalDateTime으로 생성 후 Timestamp로 변환하는 작업
            LocalDateTime currentDateTime = LocalDateTime.now();
            Timestamp currentTimestamp = Timestamp.valueOf(currentDateTime);
            log.info("Current timestamp: {}", currentTimestamp);

            // 주문 취소 기간 설정
            if(product.getRecruitmentConfirmed() && product.getRecruitmentDeadline().before(currentTimestamp)) {
                throw new IllegalArgumentException("주문 취소 기간이 지났습니다.");
            }

            paymentService.processRefund(order, "주문 취소에 따른 환불 요청");

        } catch (Exception e) {
            log.error("환불 처리 중 오류 발생 : ", e);

            throw new RuntimeException("환불 처리 중 오류 발생");
        }
        order.setRefundStatus(true);
//        orderRepository.deleteById(orderNum);
    }

    // 자동 환불
    @Transactional
    public void refundOrder(Order order) {
        Order orders = orderRepository.findById(order.getOrderNum())
                        .orElseThrow(()-> new RuntimeException("일치하는 주문번호가 없습니다"));
        try {
            paymentService.processRefund(orders, "모집인원 미달로 인한 자동 환불 요청");
        } catch (Exception e) {
            log.error("환불 처리 중 오류 발생 :", e);
            throw new RuntimeException("환불 처리 중 오류 발생");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void autoRefund() {

        LocalDateTime currentTimestamp = LocalDateTime.now();
        log.info("Current timestamp: {}", currentTimestamp);

        // 모집 기간이 지난 상품
        List<Product> products = productRepository.findUnConfirmedProductPastDeadlineBefore(currentTimestamp);
        for(Product product : products) {
            // 결제 완료 된 상품에 대한 주문들
            List<Order> orders = orderRepository.findByProductAndPaymentStatus(product, true);
            for(Order order : orders) {
                refundOrder(order);
                order.setRefundStatus(true);
                orderRepository.save(order);
            }
        }
    }

}
