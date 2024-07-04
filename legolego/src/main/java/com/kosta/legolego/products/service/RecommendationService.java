package com.kosta.legolego.products.service;

import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationService {
  @Autowired
  private ProductRepository productRepository;

  public ProductDto recommendProducts(Long productNum, String destination) {
    log.debug("추천 요청: productNum={}, destination={}", productNum, destination);

    LocalDateTime now = LocalDateTime.now();

    // 필요한 조건을 모두 만족하는 상품 조회(목적지가 일치하고, 자기 자신(상품) 제외, 모집 마감기한이 지나지 않은 것)
    List<Product> products = productRepository.findRecommendedProducts(destination, productNum, now);
    log.debug("조건을 만족하는 상품 조회 결과: {}", products);

    if (products.isEmpty()) {
      // 조건에 맞는 상품이 없으면 랜덤으로 한 개 반환
      log.debug("조건에 맞는 상품이 없음, 랜덤 상품 반환");
      return randcomProducts(productNum, now);
    }

    // 조건을 만족하는 상품 중 랜덤으로 한 개 반환
    Random random = new Random();
    ProductDto productDto = ProductDto.fromEntity(products.get(random.nextInt(products.size())));
    log.debug("조건을 만족하는 상품 중 랜덤으로 한 개 반환: {}", productDto);
    return productDto;
  }

  private ProductDto randcomProducts(Long productNum, LocalDateTime now) {
    log.debug("랜덤 상품 반환 시작: productNum={}", productNum);

    List<Product> products = productRepository.findAll();
    log.debug("모든 상품 조회 결과: {}", products);

    // 자기 자신 제외 및 모집 마감 기한 지난 것 제외
    products = products.stream()
            .filter(product -> !(product.getProductNum().equals(productNum)) && product.getRecruitmentDeadline().toLocalDateTime().isAfter(now))
            .collect(Collectors.toList());
    log.debug("필터링 후 결과: {}", products);

    if (products.isEmpty()) {
      log.error("추천 상품이 없습니다.");
      throw new IllegalArgumentException("추천 상품이 없습니다.");
    }

    // 랜덤으로 한 개 반환
    Random random = new Random();
    ProductDto productDto = ProductDto.fromEntity(products.get(random.nextInt(products.size())));
    log.debug("랜덤으로 한 개 반환: {}", productDto);
    return productDto;
  }
}