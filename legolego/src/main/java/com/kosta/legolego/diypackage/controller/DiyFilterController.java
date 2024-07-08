package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.diypackage.service.DiyFilterService;
import com.kosta.legolego.products.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/packages")
public class DiyFilterController {
  @Autowired
  DiyFilterService diyFilterService;

  //응원 달성한 패키지만 보여줌
  @GetMapping("/over-liked")
  public ResponseEntity<List<OverLikedList>> getOverLikedPackages() {
    List<OverLikedList> packages = diyFilterService.getFilteredOverLikedPackages();
    return ResponseEntity.ok(packages);
  }

  //인기순 정렬
  @GetMapping("/popular")
  public ResponseEntity<List<DiyPackage>> getOPopularPackages() {
    List<DiyPackage> packages = diyFilterService.getPopular();
    return ResponseEntity.ok(packages);
  }

  //목적지 검색
  @GetMapping("/searched-destination")
  public ResponseEntity<?> getDestinationPackages(@RequestParam("destination") String destination) {
    try {
      List<DiyPackage> packages = diyFilterService.getDestination(destination);
      return ResponseEntity.ok(packages);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error occurred while searching packages", e);
      return ResponseEntity.status(500).body("서버 오류");
    }
  }

  //월별 검색
  @GetMapping("/searched-month")
  public ResponseEntity<?> getMonthPackages(@RequestParam("month") int month) {
    try {
      List<DiyPackage> packages = diyFilterService.getMonth(month);
      return ResponseEntity.ok(packages);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error occurred while searching packages", e);
      return ResponseEntity.status(500).body("서버 오류");
    }
  }

  @GetMapping("/searched")
  public ResponseEntity<?> getSearchedPackages(@RequestParam(value = "destination", required = false) String destination, @RequestParam(value = "month", required = false) Integer month) {
    try {
      if (month == null && destination == null) {
        return ResponseEntity.badRequest().body("Month and destination cannot both be null");
      }

      if (month == null) {
        List<DiyPackage> packages = diyFilterService.getDestination(destination);
        return ResponseEntity.ok(packages);
      }

      if (destination == null) {
        List<DiyPackage> packages = diyFilterService.getMonth(month);
        return ResponseEntity.ok(packages);
      }

      List<DiyPackage> packages = diyFilterService.getDestinationAndMonth(destination, month);
      return ResponseEntity.ok(packages);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error occurred while searching packages", e);
      return ResponseEntity.status(500).body("서버 오류");
    }
  }
  //추천상품
  @GetMapping("/recommend/products")
  public ResponseEntity<?> getRecommend(@RequestParam("destination") String destination) {
    try {
      ProductDto recommendedProduct = diyFilterService.recommendProducts(destination);
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
