//package com.kosta.legolego.partner.controller;
//
//import com.kosta.legolego.admin.dto.AdminLikedListDto;
//import com.kosta.legolego.diypackage.entity.DiyList;
//import com.kosta.legolego.partner.dto.OfferFormDto;
//import com.kosta.legolego.partner.service.PartnerLikedListService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//        import java.util.List;
//
//@RestController
//public class PartnerLikedListController {
//
//    @Autowired
//    private PartnerLikedListService partnerLikedListService;
//
//    // 관리자가 승인한 패키지 리스트 조회
//    @GetMapping("/partner/approved_packages")
//    public List<AdminLikedListDto> getApprovedPackages() {
//        return partnerLikedListService.getApprovedPackages();
//    }
//
//    // 작성자에게 가격 제안하기
////    @PostMapping("/partner/approved_packages/{list_num}/submit_offer")
////    public DiyList submitOfferForm(@PathVariable("list_num") Long listNum, @RequestBody OfferFormDto offerFormDto) {
////        return partnerLikedListService.submitOfferForm(listNum, offerFormDto);
////    }
//
//    // 작성자에게 가격 제안하기
//    @PostMapping("/partner/approved_packages/{list_num}/submit_offer")
//    public DiyList submitOfferForm(@RequestBody OfferFormDto offerFormDto) {
//        return partnerLikedListService.submitOfferForm(offerFormDto);
//    }
//
//    // 가격 제안하지 않고 거절하기
//    @DeleteMapping("/partner/approved_packages/{list_num}/reject")
//    public void rejectPackage(@PathVariable("list_num") Long listNum) {
//        partnerLikedListService.rejectPackage(listNum);
//    }
//}
