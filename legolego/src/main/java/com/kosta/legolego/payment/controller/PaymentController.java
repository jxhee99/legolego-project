package com.kosta.legolego.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.orders.service.OrderService;
import com.kosta.legolego.payment.dto.PaymentRequestDto;
import com.kosta.legolego.payment.service.PaymentService;
import com.kosta.legolego.security.CustomUserDetails;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
//import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
@RequestMapping("/user/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    OrderService orderService;

    private IamportClient iamportClient; // build.gradle에 의존성 추가해야 객체 사용 가능

    @Value("${iamport.api.key}") // lombok.Value로 임포트 x
    private String apiKey;

    @Value("${iamport.api.secret")
    private String apiSecret;

    @PostConstruct
    public void init(){
        this.iamportClient = new IamportClient(apiKey, apiSecret);
    }

    // 결제 완료 요청 및 결제 실패 시 환불 처리
    @PostMapping("/complete")
    public ResponseEntity<String> paymentComplete(@AuthenticationPrincipal CustomUserDetails userDetails
,@RequestBody PaymentRequestDto paymentRequestDto) {
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String impUid = paymentRequestDto.getImpUid();
            String merchantUid = paymentRequestDto.getMerchantUid();

            log.info("결제 완료 요청 - IMP UID : {}, Merchant UID : {}", impUid, merchantUid);

            Order order = orderService.getOrderEntityByMerchantUid(merchantUid);

            // 결제 처리 로직 호출
            String paymentData = paymentService.processPayment(impUid, order.getMerchantUid());

            return ResponseEntity.ok("결제 성공");
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생 :", e);

            try {
                // 결제 실패 - 환불 로직
                String token = paymentService.getAccessToken(apiKey, apiSecret);

                String merchantUid = paymentRequestDto.getMerchantUid();
                Order order = orderService.getOrderEntityByMerchantUid(merchantUid);

                paymentService.refundWithToken(token, order.getMerchantUid(), e.getMessage());
                log.info("환불 처리 완료 : 주문 번호 {}", order.getMerchantUid());
            } catch (Exception refundException){
                log.error("환불 처리 중 오류 발생 : ", refundException);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 처리 중 오류 발생");
        }
    }

    //  결제 정보 검증
    @PostMapping("/validation/{imp_uid}")
    public ResponseEntity<String> validatePayment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PaymentRequestDto paymentRequestDto){
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String impUid = paymentRequestDto.getImpUid();
            String merchantUid = paymentRequestDto.getMerchantUid();

            Order order = orderService.getOrderEntityByMerchantUid(merchantUid);

            // portone에서 결제 정보 조회
            // Payment는 com.siot.IamportRestClient.response.Payment 클래스임
            IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(impUid);
            String paymentData = new ObjectMapper().writeValueAsString(paymentResponse);

            // 결제 정보 검증 및 저장
            paymentService.verifyAndSavePayment(impUid, order.getMerchantUid(), paymentData);
            return ResponseEntity.ok("결제 검증 및 저장 성공");
        } catch (Exception e) {
            log.error("결제 검증 및 저장 오류 발생 : ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 검증 및 저장 중 오류 발생");

        }
    }

}