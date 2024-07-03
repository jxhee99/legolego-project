package com.kosta.legolego.products.service;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.repository.AirlineRepository;
import com.kosta.legolego.diypackage.repository.DiyListRepository;
import com.kosta.legolego.diypackage.repository.DiyRepository;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Random;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
  @Autowired
  ProductRepository productRepository;
  @Autowired
  DiyListRepository diyListRepository;
  @Autowired
  DiyRepository diyRepository;
  @Autowired
  AirlineRepository airlineRepository;

  public ProductDto recommendProducts(Long productNum , String destination){
    // 1. 비행기 테이블에서 해당 정보를 조회
    List<AirlineEntity> airlines = airlineRepository.findByDestination(destination);
    if (airlines == null || airlines.isEmpty()) {
      throw new IllegalArgumentException("비행스케줄을 찾을 수 없습니다");
    }

    // 2. Diy 패키지 가져오기
    List<DiyPackage> diyPackages = airlines.stream()
            .flatMap(airline -> diyRepository.findByAirline(airline).stream())
            .collect(Collectors.toList());

    if (diyPackages.isEmpty()) {
      throw new IllegalArgumentException("DIY 패키지를 찾을 수 없습니다");
    }

    //3. Diy Package로 DIY리스트 가져오기
    List<DiyList> diyLists = diyPackages.stream()
            .flatMap(diyPackage -> diyListRepository.findByDiyPackage(diyPackage).stream())
            .collect(Collectors.toList());
    if(diyLists.isEmpty()){
      throw new IllegalArgumentException("DIY 리스트를 찾을 수 없습니다.");
    }

    //4. Diy ListNum으로 제품 테이블 조회
    List<Product> products = diyLists.stream()
            .flatMap(diyList -> productRepository.findByDiyList(diyList).stream())
            .collect(Collectors.toList());
    if(products.isEmpty()){
      throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
    }

    //5.자기 자신(상품) 제외
    products = products.stream()
            .filter(product -> !(product.getProductNum() == productNum ))
            .collect(Collectors.toList());
    //6. 모집 마감기한 지난 것 제외
    LocalDateTime now = LocalDateTime.now();
    products = products.stream()
            .filter(product -> product.getRecruitmentDeadline().toLocalDateTime().isAfter(now))
            .collect(Collectors.toList());
    if(products.isEmpty()){
      throw new IllegalArgumentException("적합한 추천 상품이 없습니다.");
    }
    // 7. 랜덤으로 한 개 반환
    Random random = new Random();
    ProductDto productDto = ProductDto.fromEntity(products.get(random.nextInt(products.size())));
    return productDto;
  }

}
