package com.kosta.legolego.orders.repository;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.products.entity.Product;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_userNum(Long userNum);
    boolean existsById(Long userNum);

    Optional<Order> findByMerchantUid(String merchantUid);
    long countByProduct(Product product);

    long countByProductAndPaymentStatus(Product product, Boolean PaymentStatus);
//    List<Order> findByUser_userNumAndProduct_productNum(Long userNum, Long productNum);


}
