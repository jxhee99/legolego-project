package com.kosta.legolego.diypackage.service;

import com.kosta.legolego.diypackage.entity.*;
import com.kosta.legolego.diypackage.repository.*;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductSearchRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Transactional
@Slf4j
@Service
public class DiyFilterService {
  @Autowired
  private OverLikedListRepository overLikedListRepository;

  @Autowired
  private DiyRepository diyRepository;

  @Autowired
  private AirlineRepository airlineRepository;

  @Autowired
  ProductSearchRepository productSearchRepository;

  @Autowired
  DiyListRepository diyListRepository;



  public List<OverLikedList> getFilteredOverLikedPackages(){
    List<OverLikedList> packages = overLikedListRepository.findAllByOrderByDiyPackageDesc();
    return packages;
  }

  public List<DiyPackage> getPopular(){
    List<DiyPackage> packages = diyRepository.findAllByPackageDraftFalseOrderByPackageLikedNumDescPackageNumDesc();
    return packages;
  }
  // 목적지 검색
  public List<DiyPackage> getDestination(String destination){
    //해당 목적지의 AirlineEntity를 가져옴
    List<AirlineEntity> airlineEntities = airlineRepository.findByDestinationContainingOrderByAirlineNumDesc(destination);
    // DiyPackage 리스트를 저장할 리스트
    List<DiyPackage> diyPackages = new ArrayList<>();

    // 각 AirlineEntity에 대해 DiyPackage를 가져와서 diyPackages에 추가
    for (AirlineEntity airline : airlineEntities) {
      DiyPackage diyPackage = diyRepository.findByAirlineAndPackageDraftFalse(airline);
      if (diyPackage != null) {
        diyPackages.add(diyPackage);
      }
    }

    return diyPackages;
  }
  // 월별 검색
  public List<DiyPackage> getMonth(int month){
    //시작일이 해당 월인 airlineEntity를 가져옴
    List<AirlineEntity> airlineEntities = airlineRepository.findByMonth(month);

    // DiyPackage 리스트를 저장할 리스트
    List<DiyPackage> diyPackages = new ArrayList<>();

    // 각 airlinEntity에 대해 diyPackage를 가져와서 반환할 리스트에 추가
    for(AirlineEntity airline : airlineEntities){
      DiyPackage diyPackage = diyRepository.findByAirlineAndPackageDraftFalse(airline);

      if (diyPackage != null) {
        diyPackages.add(diyPackage);
      }
    }
    return diyPackages;
  }
  //통합 검색
  public List<DiyPackage> getDestinationAndMonth(String destination, int month){
    List<AirlineEntity> airlineEntities = airlineRepository.findByDestinationAndMonth(destination, month);
    List<DiyPackage> diyPackages = new ArrayList<>();

    for (AirlineEntity airline : airlineEntities) {
      DiyPackage diyPackage = diyRepository.findByAirlineAndPackageDraftFalse(airline);
      if (diyPackage != null) {
        diyPackages.add(diyPackage);
      }
    }
    return diyPackages;

  }

  // 상품 추천
  public ProductDto recommendProducts(String destination){
    LocalDateTime now = LocalDateTime.now();
    List<Product> products = productSearchRepository.findBYDestinationProducts(destination, now);
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

  //여행 출발일 지났는데 상품 되지 않은 diy를 overliked, diyList에서 삭제
  @Scheduled(cron = "0 33 * * * *") //매 시간 30분에
  public void deleteOverLiked(){
    LocalDateTime now = LocalDateTime.now();

    // 1. diyListPackage를 순회하여, is_registered가 false인 패키지를 찾는다.
    List<DiyPackage> diyPackages = diyListRepository.findDiyPackagesNotRegistered();
    log.info("Found {} diyPackages not registered.", diyPackages.size());

    // 2. 해당 패키지들 중에서 여행 기간이 지난 패키지를 찾는다.
    List<DiyPackage> diysOverDeadline = new ArrayList<>();
    for (DiyPackage diyPackage : diyPackages) {
      LocalDateTime boardingDate = diyPackage.getAirline().getBoardingDate();
      if (boardingDate.isBefore(now)) {
        diysOverDeadline.add(diyPackage);
      }
    }
    log.info("Found {} diys over deadline.", diysOverDeadline.size());

    // 3. over_liked 테이블에서 삭제
    for (DiyPackage diyPackage : diysOverDeadline) {
      overLikedListRepository.deleteByDiyPackage(diyPackage);
      log.info("Deleted from overLikedList: {}", diyPackage.getPackageNum());
    }

    // 4. diyList에서도 삭제
    for (DiyPackage diyPackage : diysOverDeadline) {
      diyListRepository.deleteByDiyPackage(diyPackage);
      log.info("Deleted from diyList: {}", diyPackage.getPackageNum());
    }
  }

}
