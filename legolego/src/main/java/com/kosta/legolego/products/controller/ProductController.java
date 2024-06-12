package com.kosta.legolego.products.controller;

import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kosta.legolego.products.service.ProductService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

//  상품 전체 조회
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

//  상품 상세 조회
    @GetMapping("/{product_num}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("product_num") Long product_num){
        ProductDto productDto = productService.getProductById(product_num);
        return ResponseEntity.status(HttpStatus.OK).body(productDto);
    }

    //  상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("keyword") String keyword){
        List<ProductDto> prdoucts = productService.searchProducts(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(prdoucts);
    }

//    //  찜 하기
//    @PostMapping("/{product_num}/loved")
//    public ResponseEntity<Void> lovedProduct(@PathVariable("product_num") Long product_num){
//        productService.lovedProduct(product_num);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//
//    //  찜 취소
//    @DeleteMapping("/{product_num}/loved")
//    public ResponseEntity<Void> unlovedProduct(@PathVariable("product_num") Long product_num){
//        productService.unlovedProduct(product_num);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }



}