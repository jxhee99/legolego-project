import http from 'k6/http';
import {check, sleep } from 'k6';

export let options = {
    stages : [
        {duration : '30s', target : 100}, // 30초동안 100명의 사용자 증가
        {duration : '1m', target : 100}, // 1분동안 100명의 사용자 유지
        {duration : '10s', target : 0}, // 10초동안 0명으로 감소
    ],
    // Prometheus 출력 설정
        thresholds: {
            failed_requests: ['rate<0.01'], // 1% 미만
            http_req_duration: ['p(95)<500'], // 95% 요청이 500ms 이내
        },
        ext: {
            loadimpact: {
                projectID: 12345,
                name: 'Test Project',
            },
            metrics: {
                myFailRate,
            },
        },
};


export default function () {
  // 로그인 요청
  let loginRes = http.post('http://localhost:8080/auth/login', JSON.stringify({
    email: 'user2@test.com',
    password: '12341234',
  }), {
    headers: { 'Content-Type': 'application/json' },
  });

  check(loginRes, {
    'login status is 200': (r) => r.status === 200,
  });

  let authToken = loginRes.json('accessToken');

  // 상품 조회
  let productRes = http.get('http://localhost:8080/products/29'); // 예시 URL
  check(productRes, {
    'product status is 200': (r) => r.status === 200,
  });

  let product = productRes.json();

    // 주문 생성 요청
    let orderRes = http.post('http://localhost:8080/user/orders', JSON.stringify({
        productNum: product.productNum,
        quantity: 1,
        }), {
        headers: {
        'Content-Type' : 'application/json',
        'Authorization': `Bearer ${authToken}`,
        },
        });

        check(orderRes, {
            'Order status is 201' : (r) => r.status === 201,
        });

    if(orderRes.status === 201) {
    let orderNumber = orderRes.json('merchantUid');

        // 모의 결제 요청
          let paymentRes = http.post('http://localhost:8080/mock-payment', JSON.stringify({
            OrderId: orderNumber,
          }), {
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${authToken}`,
            },
          });

          check(paymentRes, {
            'payment status is 200': (r) => r.status === 200,
          });
      }

        sleep(1);
}