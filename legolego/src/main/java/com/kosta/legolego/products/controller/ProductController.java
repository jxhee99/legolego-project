package com.kosta.legolego.products.controller;

import com.kosta.legolego.products.dto.ProductDetailDto;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.dto.WishlistDto;
import com.kosta.legolego.products.service.WishlistService;
import com.kosta.legolego.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.kosta.legolego.products.service.ProductService;

import java.util.List;

@Slf4j
@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    WishlistService wishlistService;

//  상품 전체 조회
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

//  상품 상세 조회
    @GetMapping("/products/{product_num}")
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable("product_num") Long productNum){
        ProductDetailDto productDetailDto = productService.getProductByDetailId(productNum);
        return ResponseEntity.status(HttpStatus.OK).body(productDetailDto);
    }

    //  상품 검색
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("keyword") String keyword){
        List<ProductDto> prdoucts = productService.searchProducts(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(prdoucts);
    }

    //  특정 상품 찜 count up & 사용자 찜 목록에서 추가
    @PostMapping("/user/products/{product_num}/wishlist")
    public ResponseEntity<Void> addToWishlist(@PathVariable("product_num") Long productNum, @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userNum = userDetails.getId();
        wishlistService.addToWishlist(userNum, productNum);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 사용자의 찜 목록 조회
    @GetMapping("/user/products/wishlist")
    private ResponseEntity<List<WishlistDto>> getWishlist(@AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userNum = userDetails.getId();
        List<WishlistDto> wishlist = wishlistService.getWishlistByUser(userNum);
        return new ResponseEntity<>(wishlist, HttpStatus.OK);
    }

    //  특정 상품 찜 count down & 사용자 찜 목록에서 제거
    @DeleteMapping("/user/products/{product_num}/wishlist")
    public ResponseEntity<Void> removeFromWishlist(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("product_num") Long product_num){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userNum = userDetails.getId();
        wishlistService.removeFromWishlist(userNum, product_num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 페이지가 로드될 때 특정 상품이 찜 상태 확인
    @GetMapping("/user/products/{product_num}/wishlist/status")
    public ResponseEntity<Boolean> isProductInWishlist(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("product_num") Long productNum){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userNum = userDetails.getId();
        boolean isWishlist = wishlistService.isProductInWishlist(userNum, productNum);
        return ResponseEntity.ok(isWishlist);
    }
}