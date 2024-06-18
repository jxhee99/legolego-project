document.addEventListener("DOMContentLoaded", function() {
    $('#btn_payment').click(function() {
        // 주문 정보 생성 요청
        $.ajax({
            url: '/orders',
            method: 'POST',
            data: JSON.stringify({
                userNum: 1, // 예시로 설정한 사용자 ID (실제로는 적절한 값을 설정)
                userName: "김민지",
                userEmail: "wngml2666@naver.com",
                userPhone: "010-1111-1111",
                productNum: 1, // 예시로 설정한 제품 ID (실제로는 적절한 값을 설정)
                amount: 2, // 예시로 설정한 수량
                price: 50, // 예시로 설정한 가격
                totalPrice: amount * price // 예시로 설정한 총 가격
            }),
            contentType: 'application/json',
            success: function(response) {
                // 서버에서 반환된 주문 정보에서 merchant_uid와 기타 정보를 가져옴
                var merchant_uid = response.orderNumber;
                console.log("주문 생성 성공 - Merchant UID: " + merchant_uid);

                // 주문 정보를 다시 가져와서 결제창에 반영
                $.ajax({
                    url: '/orders/merchant/' + merchant_uid,
                    method: 'GET',
                    success: function(order) {
                        // 주문 정보가 성공적으로 반환된 경우
                        var userEmail = order.userEmail;
                        var userName = order.userName;
                        var userPhone = order.userPhone;
                        var productName = order.productName;
                        var totalPrice = order.totalPrice;

                        // 결제 요청
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
                                console.log("결제 성공 - Imp UID: " + rsp.imp_uid);
                                // 결제 성공 시 결제 정보 저장
                                $.ajax({
                                    url: '/payments/complete',
                                    method: 'POST',
                                    data: JSON.stringify({
                                        impUid: rsp.imp_uid,
                                        merchantUid: merchant_uid
                                    }),
                                    contentType: 'application/json',
                                    success: function() {
                                        alert('결제가 완료되었습니다.');
                                    },
                                    error: function() {
                                        alert('결제 정보 저장에 실패하였습니다.');
                                        console.log("결제 정보 저장 실패 - Merchant UID: " + merchant_uid);
                                        console.log("결제 정보 저장 실패 - Imp UID: " + rsp.imp_uid);
                                    }
                                });
                            } else {
                                alert('결제를 실패하였습니다.');
                            }
                        });
                    },
                    error: function() {
                        alert('주문 정보를 가져오는 데 실패했습니다.');
                        console.log("주문 고유 번호는 : " + merchant_uid);
                    }
                });
            },
            error: function() {
                alert('주문 생성에 실패하였습니다.');
            }
        });
    });
});