package com.kosta.legolego.payment.repository;

import com.kosta.legolego.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
//    List<Payment> findpaymentByImpUid(String impUid);
}
