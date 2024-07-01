package com.kosta.legolego.orders.repository;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.products.entity.Product;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_userNum(Long userNum);
    boolean existsById(Long userNum);

    Optional<Order> findByMerchantUid(String merchantUid);

    // 모집 확정 업데이트 위한 메서드
    long countByProductAndPaymentStatus(Product product, Boolean PaymentStatus);

    // 자동 환불 처리 되어야 하는 상품
    List<Order> findByProductAndPaymentStatus(Product product, Boolean paymentStatus);
}
