package com.kosta.legolego.partner.service;

import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.diypackage.repository.OverLikedListRepository;
import com.kosta.legolego.partner.repository.PartnerLikedListRepository;
import com.kosta.legolego.partner.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            .orElseThrow(() -> new NullPointerException("찾을 수 없는 파트너 번호"));

    //모든 항목을 조회
    List <OverLikedList> overLikedListAll = overLikedListRepository.findAll();

    //DiyList 테이블에서 partner가 일치하는 항목 조회
    List<DiyList> diyListForPartner = partnerLikedListRepository.findByPartner(partner);

    //DiyList 항목의 package 목록 추출
    Set<DiyPackage> diyPackages = diyListForPartner.stream().map(DiyList::getDiyPackage).collect(Collectors.toSet());

    //overLikedListAll에서 DiyList의 packageNum과 일치하지 않는 항목 필터링
    List<OverLikedList> filteredOverLikedList = overLikedListAll.stream()
            .filter(overLiked -> !diyPackages.contains(overLiked.getDiyPackage()))
            .collect(Collectors.toList());

    return filteredOverLikedList;

  }
}
