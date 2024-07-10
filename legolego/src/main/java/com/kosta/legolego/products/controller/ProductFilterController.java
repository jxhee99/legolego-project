package com.kosta.legolego.products.controller;

import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.service.ProductFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductFilterController {

    @Autowired
    ProductFilterService productFilterService;

    // 모집 임박 상품 조회
    @GetMapping("/recruitmentClose")
    public ResponseEntity<List<ProductDto>> getRecruitmentCloseProduct() {
        List<ProductDto> products = productFilterService.getRecruitmentCloseProduct();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // 마감 임박 상품 조회
    @GetMapping("/sortByDeadlineDesc")
    public ResponseEntity<List<ProductDto>> getSortByDeadlineDescProduct() {
        List<ProductDto> products = productFilterService.getSortByDeadlineDescProduct();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // 모집 확정 상품 조회
    @GetMapping("/recruitmentConfirmed")
    public ResponseEntity<List<ProductDto>> getRecruitmentConfirmProduct() {
        List<ProductDto> products = productFilterService.getRecruitmentConfirmProduct();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // 최신 등록 상품들
    @GetMapping("/sortByRegDateDesc")
    public ResponseEntity<List<ProductDto>> getSortByRegDateDescProduct() {
        List<ProductDto> products = productFilterService.getSortByRegDateDescProduct();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // 주문 내역 많은 상품들(인기순)
    @GetMapping("/sortByPoplar")
    public ResponseEntity<List<ProductDto>> getSortByPoplarProduct() {
        List<ProductDto> products = productFilterService.getSortByPoplarProduct();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // 가격 높은 상품 순서
    @GetMapping("/sortByPriceDesc")
    public ResponseEntity<List<ProductDto>> getSortByPriceDescProduct() {
        List<ProductDto> products = productFilterService.getSortByPriceDescProduct();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    // 가격 낮은 상품 순서
    @GetMapping("/sortByPriceAsc")
    public ResponseEntity<List<ProductDto>> getSortByPriceAscProduct() {
        List<ProductDto> products = productFilterService.getSortByPriceAscProduct();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }
}
