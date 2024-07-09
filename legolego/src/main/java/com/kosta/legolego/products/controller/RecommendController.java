package com.kosta.legolego.products.controller;


import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

  //목적지 검색
  @GetMapping("/products/searched-destination")
  public ResponseEntity<?> searchDestinationProducts(@RequestParam("destination") String destination) {
    try {
      List<ProductDto> products = recommendationService.searchDestination(destination);
      return ResponseEntity.ok(products);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error occurred while searching product", e);
      return ResponseEntity.status(500).body("서버 오류");
    }
  }

  //월 별 검색
  @GetMapping("/products/searched-month")
  public ResponseEntity<?> searchMonthProducts(@RequestParam("month") int month) {
    try {
      List<ProductDto> products = recommendationService.searchMonth(month);
      return ResponseEntity.ok(products);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error occurred while searching product", e);
      return ResponseEntity.status(500).body("서버 오류");
    }
  }

  //통합 검색
  @GetMapping("/products/searched")
  public ResponseEntity<?> searchedProducts(@RequestParam(value = "destination", required = false) String destination, @RequestParam(value = "month", required = false) Integer month) {
    try {
      if (month == null && destination == null) {
        return ResponseEntity.badRequest().body("Month and destination cannot both be null");
      }

      if (month == null) {
        List<ProductDto> products = recommendationService.searchDestination(destination);
        return ResponseEntity.ok(products);
      }

      if (destination == null) {
        List<ProductDto> products = recommendationService.searchMonth(month);
        return ResponseEntity.ok(products);
      }

      List<ProductDto> products = recommendationService.searchDestinationAndMonth(destination, month);
      return ResponseEntity.ok(products);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error occurred while searching products", e);
      return ResponseEntity.status(500).body("서버 오류");
    }
  }
}









