package com.kosta.legolego.partner.service;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.partner.dto.OfferFormDto;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.diypackage.repository.OverLikedListRepository;
import com.kosta.legolego.partner.repository.PartnerLikedListRepository;
import com.kosta.legolego.partner.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PartnerPackageService {
  @Autowired
  OverLikedListRepository overLikedListRepository;
  @Autowired
  PartnerLikedListRepository partnerLikedListRepository;

  @Autowired
  private PartnerRepository partnerRepository; // Partner 엔티티를 조회하기 위해 필요

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
    List<OverLikedList> filteredOverLikedList = overLikedListRepository.findAll().stream()
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

    return partnerLikedListRepository.save(diyList);
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
