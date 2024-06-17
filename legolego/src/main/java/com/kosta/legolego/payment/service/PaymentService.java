package com.kosta.legolego.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.payment.entity.Payment;
import com.kosta.legolego.payment.repository.PaymentRepository;
//import com.siot.IamportRestClient.IamportClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

//    private final IamportClient api;
//
//    public PaymentService(){
//        this.api = new IamportClient("6556015464164602", "MEMrF1IW43bg0uC9bmNtbjcCnfOZHbq3tXwaJwbIruxkPqfiDwJ1RMIvaqKaO6Phd6u2EG1RHqmaN9OC");
//    }

    @Value("${iamport.api.key}")
    private String apiKey;

    @Value("${iamport.api.secret}")
    private String apiSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

//    결제 처리 로직
    public String processPayment(String impUid, String merchantUid) throws Exception {
        // 액세스 토큰 발급 받기
        String accessToken = getAccessToken();

        // imp_uid로 포트원 서버에서 결제 정보 조회
        String paymentData = getPaymentData(impUid, accessToken);

        // 결제 정보 검증 및 저장
        verifyAndSavePayment(impUid, merchantUid, paymentData);

        // 결제 정보 반환
        return paymentData;
    }

//    포트원 api 사용해서 엑세스 토큰 발급 받기
    private String getAccessToken() throws Exception {
        String url = "https://api.iamport.kr/users/getToken"; // portone 토큰 발급 api 엔드포인트

        // 요청 본문 생성
        // ObjectMapper : TokenRequest 객체를 JSON 문자열로 반환
        String requestJson = new ObjectMapper().writeValueAsString(new TokenRequest(apiKey, apiSecret));

        HttpHeaders headers = new HttpHeaders(); // HTTP 요청 헤더 설정
        headers.setContentType(MediaType.APPLICATION_JSON); // HTTP 헤더의 Content-Type을 application/json으로 설정하여 요청 본문이 JSON 형식임을 지정

        // JSON 문자열로 변환된 요청 본문과 헤더를 포함하는 HttpEntity 객체를 생성
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers); // 실제 HTTP 요청에 사용됨

        // restTemplate 객체의 exchange 메서드 사용 - Iamport 토큰 발급 API에 POST 요청
        // 요청 URL, HTTP 메서드, 요청 본문과 헤더를 포함하는 HttpEntity 객체, 그리고 응답 타입을 지정 한 후  ResponseEntity<String> 객체로 반환
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 응답 본문에서 access_token 추출
        JsonNode rootNode = new ObjectMapper().readTree(response.getBody()); // readTree() : 응답 본문을 JSON으로 파싱하기 위해 응답 본문을 JsonNode 트리 구조로 변환
        return rootNode.path("response").path("access_token").asText(); // response 필드의 하위 필드인 access_token 값을 추출하여 문자열로 반환
    }

//    포트원 api 사용해서 결제 정보 조회
    private String getPaymentData(String impUid, String accessToken) throws Exception {
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // 결제 정보 반환
        return response.getBody();
    }
    // 결제 정보 검증 및 저장
    private void verifyAndSavePayment(String impUid, String merchantUid, String paymentData) throws Exception {
        JsonNode paymentJson = new ObjectMapper().readTree(paymentData);
        String status = paymentJson.path("response").path("status").asText();
        String receivedMerchantUid = paymentJson.path("response").path("merchant_uid").asText();
        BigDecimal amount = new BigDecimal(paymentJson.path("response").path("amount").asText());

        // 주문 정보 조회
        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 결제 정보 검증
        if (!receivedMerchantUid.equals(merchantUid) || amount.compareTo(order.getTotalPrice()) != 0) {
            throw new RuntimeException("Payment validation failed");
        }

        // 결제 정보 저장
        Payment payment = new Payment();
//        payment.setOrder(order);
//        payment.setImpUid(impUid);
        payment.setPaymentMethod("card"); // 예시로 카드 결제 방식 설정
        payment.setStatus(status);

        order.setPaymentStatus(true);
        orderRepository.save(order);
        paymentRepository.save(payment);
    }

    @Getter
    @Setter
    private static class TokenRequest {
        private String imp_key;
        private String imp_secret;

//        엑세스 토큰 요청 본문 생성 : 내부 클래스
        public TokenRequest(String impKey, String impSecret) {
            this.imp_key = impKey;
            this.imp_secret = impSecret;
        }
    }

}