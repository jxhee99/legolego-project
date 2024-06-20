package com.kosta.legolego.products.controller;


import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.products.dto.ProductDetailDto;
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

//  관리자 상품 전체 조회
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProductsForAdmin(){
        List<ProductDto> products = productService.getAllProductsForAdmin();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

//  관리자 상품 상세 조회
    @GetMapping("/{product_num}")
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable("product_num") Long productNum) {
        ProductDetailDto productDetailDto = productService.getProductByDetailId(productNum);
        return ResponseEntity.status(HttpStatus.OK).body(productDetailDto);
    }

//  정식 상품 수정 (only admin)
    @PatchMapping("/{product_num}/{admin_num}/edit")
    public ResponseEntity<ProductDto> updateProductById(@PathVariable("product_num") Long productNum, @PathVariable("admin_num") Long adminNum, @RequestBody ProductDto productDto){

        try {
            ProductDto updatedProduct = productService.updateProduct(productNum, productDto, adminNum);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
//        return productService.updateProduct(product_num, productDto);
    }

//  정식 상품 삭제 (only admin)
    @DeleteMapping("/{product_num}/{admin_num}/delete")
    public void deleteProduct(@PathVariable("product_num") Long productNum, @PathVariable("admin_num") Long adminNum){
        productService.deleteProduct(productNum, adminNum);
    }


}
