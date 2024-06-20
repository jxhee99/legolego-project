package com.kosta.legolego.products.controller;

import com.kosta.legolego.products.dto.ProductDetailDto;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.dto.WishlistDto;
import com.kosta.legolego.products.service.WishlistService;
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

    @Autowired
    WishlistService wishlistService;

//  상품 전체 조회
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

//  상품 상세 조회
    @GetMapping("/{product_num}")
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable("product_num") Long productNum){
        ProductDetailDto productDetailDto = productService.getProductByDetailId(productNum);
        return ResponseEntity.status(HttpStatus.OK).body(productDetailDto);
    }

    //  상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("keyword") String keyword){
        List<ProductDto> prdoucts = productService.searchProducts(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(prdoucts);
    }

    //  특정 상품 찜 count up & 사용자 찜 목록에서 추가
    @PostMapping("/{product_num}/wishlist")
    public ResponseEntity<Void> addToWishlist(@PathVariable("product_num") Long product_num, @RequestParam("user_num") Long user_num){
        wishlistService.addToWishlist(user_num, product_num);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 사용자의 찜 목록 조회
    @GetMapping("/{user_num}/wishlist")
    private ResponseEntity<List<WishlistDto>> getWishlist(@PathVariable("user_num") Long user_num){
        List<WishlistDto> wishlist = wishlistService.getWishlistByUser(user_num);
        return new ResponseEntity<>(wishlist, HttpStatus.OK);
    }

    //  특정 상품 찜 count down & 사용자 찜 목록에서 제거
    @DeleteMapping("/{product_num}/wishlist")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable("product_num") Long product_num, @RequestParam("user_num") Long user_num){
        wishlistService.removeFromWishlist(user_num, product_num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}