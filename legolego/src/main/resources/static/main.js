document.addEventListener("DOMContentLoaded", function() {
function requestPay() {
    // 결제 정보
    var userNum = 4; // 예시로 설정한 사용자 ID (실제로는 적절한 값을 설정)
    var productNum = 9; // 예시로 설정한 제품 ID (실제로는 적절한 값을 설정)
    var amount = 1; // 예시로 설정한 수량
    var price = 99.99;
    var totalPrice = price * amount; // 예시로 설정한 총 가격
    var productName = "Test Product1"; // 예시로 설정한 제품명
    var userEmail = "user1@example.com"; // 예시로 설정한 구매자 이메일
    var userName = "User One"; // 예시로 설정한 구매자 이름
    var userPhone = "010-1234-5678"; // 예시로 설정한 구매자 전화번호

    // 주문 정보 생성 요청
    $.ajax({
        url: '/orders',
        method: 'POST',
        data: JSON.stringify({
            userNum: userNum,
            productNum: productNum,
            amount: amount,
            price: price,
            totalPrice: totalPrice
        }),
        contentType: 'application/json',
        success: function(response) {
        console.log("주문 생성 응답 : " , response);
            // 서버에서 반환된 주문 정보에서 merchant_uid와 기타 정보를 가져옴
            var merchant_uid = response.orderNumber;

            var IMP = window.IMP;
            IMP.init('imp01063088'); // 가맹점 식별코드 입력
            IMP.request_pay({
                pg: 'html5_inicis',
                pay_method: 'card',
                merchant_uid: merchant_uid,
                name: productName,
                amount: totalPrice,
                buyer_email: userEmail,
                buyer_name: userName,
                buyer_tel: userPhone
            }, function (rsp) {
                if (rsp.success) {
                    // 결제 성공 시 결제 정보 저장
                    $.ajax({
                        url: '/payments/complete',
                        method: 'POST',
                        data: JSON.stringify({
                            orderNumber: response.orderNumber, // 실제로는 서버에서 반환된 orderId 사용
                            impUid: rsp.imp_uid,
                            paymentMethod: rsp.pay_method,
                            status: 'completed'
                        }),
                        contentType: 'application/json',
                        success: function() {
                            alert('결제가 완료되었습니다.');
                        },
                        error: function() {
                            alert('결제 정보 저장에 실패하였습니다.');
                        }
                    });
                } else {
                    alert('결제를 실패하였습니다.');
                }
            });
        },
        error: function() {
            alert('주문 생성에 실패하였습니다.');
        }
    });
}
    // requestPay 함수를 전역 범위에 노출시켜 HTML에서 접근할 수 있게 함
    window.requestPay = requestPay;
});