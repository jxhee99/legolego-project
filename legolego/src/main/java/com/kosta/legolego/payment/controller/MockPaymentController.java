package com.kosta.legolego.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MockPaymentController {

    // 모의 결제 로직
    @PostMapping("/mock-payment")
    public ResponseEntity<Map<String, Object>> mockPayment(@RequestBody Map<String, Object> paymentRequest) {
        // 모의 결제 로직
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Mock payment successful");

        return ResponseEntity.ok(response);
    }
}
