package com.kosta.legolego.products.controller;


import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    ProductService productService;


//   상품 등록

//  관리자 상품 전체 조회
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProductsForAdmin(){
        List<ProductDto> products = productService.getAllProductsForAdmin();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }
//  관리자 상품 상세 조회
    @GetMapping("/{product_num}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("product_num") Long productNum) {
        ProductDto productDto = productService.getProductById(productNum);
        return ResponseEntity.status(HttpStatus.OK).body(productDto);
    }

//  정식 상품 수정
    @PatchMapping("/{product_num}/edit")
    public ResponseEntity<ProductDto> updateProductById(@PathVariable("product_num") Long product_num, @RequestBody ProductDto productDto){
        try {
            ProductDto updatedProduct = productService.updateProduct(product_num, productDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
//        return productService.updateProduct(product_num, productDto);
    }

//  정식 상품 삭제
    @DeleteMapping("/{product_num}")
    public void deleteProduct(@PathVariable("product_num") Long product_num){
        productService.deleteProduct(product_num);
    }


}
