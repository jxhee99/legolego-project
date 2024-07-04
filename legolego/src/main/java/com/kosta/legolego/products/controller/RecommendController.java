package com.kosta.legolego.products.controller;

import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RecommendController {
  @Autowired
  RecommendationService recommendationService;

  @GetMapping("/recommend/{product_num}")
  public ResponseEntity<?> getRecommend(@PathVariable("product_num") Long productNum, @RequestParam("destination") String destination) {
    try {
      ProductDto recommendedProduct = recommendationService.recommendProducts(productNum, destination);
      return ResponseEntity.ok(recommendedProduct);
    } catch (IllegalArgumentException e) {
      // 추천 상품이 없을 때 204 No Content 반환
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error occurred while recommending product", e);
      return ResponseEntity.status(500).body("서버 오류");
    }
  }
}








