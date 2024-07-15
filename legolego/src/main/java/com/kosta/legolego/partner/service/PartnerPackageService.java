package com.kosta.legolego.partner.service;

import com.kosta.legolego.alarm.service.AlarmService;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.repository.DiyListRepository;
import com.kosta.legolego.diypackage.repository.DiyRepository;
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.partner.dto.OfferFormDto;
import com.kosta.legolego.partner.dto.PartnerOrderDto;
import com.kosta.legolego.partner.dto.PartnerProductDto;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.diypackage.repository.OverLikedListRepository;
import com.kosta.legolego.partner.repository.PartnerLikedListRepository;
import com.kosta.legolego.partner.repository.PartnerOrderRepository;
import com.kosta.legolego.partner.repository.PartnerRepository;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class PartnerPackageService {
  @Autowired
  OverLikedListRepository overLikedListRepository;
  @Autowired
  PartnerLikedListRepository partnerLikedListRepository;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  PartnerOrderRepository orderRepository;
  @Autowired
  DiyRepository diyRepository;

  @Autowired
  private PartnerRepository partnerRepository; // Partner 엔티티를 조회하기 위해 필요

  @Autowired
  private AlarmService alarmService; // 제안요청 알림을 위해 필요

  //좋아요 25개 넘은 리스트 조회(이미 가격 제안한 패키지는 제외시켜야 함)
  public List<OverLikedList> getOverLikedList(Long partnerNum){
    // Partner 엔티티 조회
    Partner partner = partnerRepository.findById(partnerNum)
            .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 파트너 번호: " + partnerNum));

    // 파트너와 관련된 DiyPackage들 조회
    Set<DiyPackage> diyPackages = findDiyPackagesForPartner(partner);

    // isSelected가 true인 DiyPackage들 조회
    Set<DiyPackage> selectedPackages = findSelectedDiyPackages();

    // OverLikedList 필터링
    List<OverLikedList> filteredOverLikedList = overLikedListRepository.findAllByOrderByOverLikedListNumDesc().stream()
            .filter(overLikedList -> !diyPackages.contains(overLikedList.getDiyPackage()))
            .filter(overLikedList -> !selectedPackages.contains(overLikedList.getDiyPackage()))
            .collect(Collectors.toList());

    return filteredOverLikedList;

  }

  public DiyList submitOfferForm(OfferFormDto offerFormDto, Long partnerNum) {
    //파트너Num으로 해당 파트너 조회
    Partner partner = partnerRepository.findById(partnerNum)
            .orElseThrow(() ->  new IllegalArgumentException("찾을 수 없는 파트너 번호: " + partnerNum));

    //OfferFormDto의 파트너 셋팅
    offerFormDto.setPartner(partner);

    //OfferFormDto를 엔티티로 변환
    DiyList diyList = offerFormDto.toEntity(offerFormDto);

    //등록시간 설정
    Timestamp now = Timestamp.from(Instant.now());
    diyList.setRegDate(now);

    DiyList savedDiyList = partnerLikedListRepository.save(diyList);
    log.info("DiyList saved: {}", savedDiyList);

    // DiyPackage 설정
    DiyPackage diyPackage = diyRepository.findById(offerFormDto.getPackageNum())
            .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 패키지 번호: " + offerFormDto.getPackageNum()));
    offerFormDto.setPackageNum(diyPackage.getPackageNum());

    // 제안 요청 알림 보내기
    alarmService.sendAlarmList(savedDiyList, diyPackage); // 비동기 처리

    return savedDiyList;
    }


    public List<PartnerProductDto> getProductOrders(Long partnerNum){
      //파트너Num으로 해당 파트너 조회
      Partner partner = partnerRepository.findById(partnerNum)
              .orElseThrow(() ->  new IllegalArgumentException("찾을 수 없는 파트너 번호: " + partnerNum));
      //파트너와 isRegistered true 기준으로 리스트 조회
      List<DiyList> diyListForPartner = partnerLikedListRepository.findByPartnerAndIsRegisteredTrue(partner);
      // 각 DiyList의 productNum으로 Product 조회 및 ProductDto 생성
      return diyListForPartner.stream()
              .map(diy -> {
                Long productNum = diy.getProductNum(); // DiyList에서 productNum 가져오기
                Product product = productRepository.findById(productNum)
                        .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 제품 번호: " + productNum));
                List<Order> orderList = orderRepository.findByProduct(product);
                List<PartnerOrderDto> orderDtoList = PartnerOrderDto.toDtoList(orderList);
                return PartnerProductDto.toDto(product, orderDtoList);
              })
              .collect(Collectors.toList());
    }

  // 파트너와 관련된 DiyPackage들 조회
  private Set<DiyPackage> findDiyPackagesForPartner(Partner partner) {
    List<DiyList> diyListForPartner = partnerLikedListRepository.findByPartner(partner);
    return diyListForPartner.stream()
            .map(DiyList::getDiyPackage)
            .collect(Collectors.toSet());
  }

  // isSelected가 true인 DiyPackage들 조회
  private Set<DiyPackage> findSelectedDiyPackages() {
    List<DiyList> selectedDiyList = partnerLikedListRepository.findByIsSelected(true);
    return selectedDiyList.stream()
            .map(DiyList::getDiyPackage)
            .collect(Collectors.toSet());
  }

}
