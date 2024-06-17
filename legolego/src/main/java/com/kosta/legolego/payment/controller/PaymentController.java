package com.kosta.legolego.payment.controller;

import com.kosta.legolego.payment.dto.PaymentRequestDto;
import com.kosta.legolego.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 결제 완료 요청
    @PostMapping("/complete")
    public ResponseEntity<String> completePayment(@RequestBody PaymentRequestDto paymentRequest) {
        try {
            String impUid = paymentRequest.getImpUid();
            String merchantUid = paymentRequest.getMerchantUid();

            // 결제 처리 로직 호출
            String paymentData = paymentService.processPayment(impUid, merchantUid);

            return ResponseEntity.ok("결제 성공");
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 처리 중 오류 발생");
        }
    }

}