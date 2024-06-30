package com.kosta.legolego.partner.repository;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartnerOrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByProduct(Product product);
}
