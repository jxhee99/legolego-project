package com.kosta.legolego.products.repository;

import com.kosta.legolego.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
//    검색 기능 쿼리 메서드 생성
   List<Product> findByProductNameContaining(String keyword);
//    findById 는 JpaRepository 에서 기본 제공
}
