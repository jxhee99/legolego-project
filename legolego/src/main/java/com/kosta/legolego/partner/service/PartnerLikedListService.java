//package com.kosta.legolego.partner.service;
//
//import com.kosta.legolego.admin.dto.AdminLikedListDto;
//import com.kosta.legolego.diypackage.entity.DiyList;
//import com.kosta.legolego.diypackage.entity.DiyPackage;
//import com.kosta.legolego.partner.dto.OfferFormDto;
//import com.kosta.legolego.partner.repository.PartnerLikedListRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class PartnerLikedListService {
//
//    @Autowired
//    private PartnerLikedListRepository partnerLikedListRepository;
//
//    // 관리자가 승인한 패키지 리스트 조회
//    public List<AdminLikedListDto> getApprovedPackages() {
//        List<DiyList> diyLists = partnerLikedListRepository.findByDiyPackage_PackageApprovalTrue();
//        return diyLists.stream()
//                .map(diyList -> new AdminLikedListDto(diyList.getDiyPackage()))
//                .collect(Collectors.toList());
//    }
//
//    // 작성자에게 가격 제안하기
////    public DiyList submitOfferForm(Long listNum, OfferFormDto offerFormDto) {
////        DiyList diyList = partnerLikedListRepository.findById(listNum)
////                .orElseThrow(() -> new IllegalArgumentException("Invalid list ID: " + listNum));
////
////        DiyPackage diyPackage = diyList.getDiyPackage();
////
////        diyList.setPrice(offerFormDto.getPrice());
////        diyList.setNecessaryPeople(offerFormDto.getNecessaryPeople());
////        diyList.setSpecialBenefits(offerFormDto.getSpecialBenefits());
//////        diyList.setModDate(new Timestamp(System.currentTimeMillis()));
////        diyList.setDiyPackage(diyPackage);
////
////
////        return partnerLikedListRepository.save(diyList);
////    }
//
//    // 작성자에게 가격 제안하기
//    public DiyList submitOfferForm(OfferFormDto offerFormDto) {
//        Long listNum = offerFormDto.getListNum();
//        DiyList diyList = partnerLikedListRepository.findById(listNum)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid list ID: " + listNum));
//
//        diyList.setPrice(offerFormDto.getPrice());
//        diyList.setNecessaryPeople(offerFormDto.getNecessaryPeople());
//        diyList.setSpecialBenefits(offerFormDto.getSpecialBenefits());
////        diyList.setModDate(new Timestamp(System.currentTimeMillis()));
//        diyList.setIsOfferSent(true);
//
//        DiyPackage diyPackage = diyList.getDiyPackage();
//        diyPackage.setPackageNum(offerFormDto.getPackageNum());
//        diyPackage.setUser(offerFormDto.getUser());
//
//        return partnerLikedListRepository.save(diyList);
//    }
//
//    // 가격 제안하지 않고 거절하기
//    public void rejectPackage(Long listNum) {
//        DiyList diyList = partnerLikedListRepository.findById(listNum)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid list ID: " + listNum));
//
//        diyList.getDiyPackage().setPackageApproval(false);
//
//        partnerLikedListRepository.save(diyList);
//    }
//}
