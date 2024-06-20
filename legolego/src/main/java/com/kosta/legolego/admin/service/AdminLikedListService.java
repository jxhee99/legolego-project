//package com.kosta.legolego.admin.service;
//
//import com.kosta.legolego.admin.dto.AdminLikedListDto;
//import com.kosta.legolego.admin.repository.AdminLikedListRepository;
//import com.kosta.legolego.diypackage.entity.DiyList;
//import com.kosta.legolego.partner.repository.PartnerLikedListRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class AdminLikedListService {
//
//    @Autowired
//    private AdminLikedListRepository adminLikedListRepository;
//
//    @Autowired
//    private PartnerLikedListRepository partnerLikedListRepository;
//
//    //    @Transactional(readOnly = true)
//    public List<AdminLikedListDto> getLikedList(Integer packageLikedNum) {
//        List<DiyList> diyLists = adminLikedListRepository.findAllByDiyPackage_PackageLikedNumGreaterThanEqual(packageLikedNum);
//        return diyLists.stream()
//                .map(diyList -> new AdminLikedListDto(diyList.getDiyPackage()))
//                .collect(Collectors.toList());
//    }
//
//    public AdminLikedListDto approvePackage(Long listNum) {
//        DiyList diyList = adminLikedListRepository.findById(listNum)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid package ID: " + listNum));
//
//        diyList.getDiyPackage().setPackageApproval(true);
//        DiyList updatedDiyList = adminLikedListRepository.save(diyList);
//        return new AdminLikedListDto(updatedDiyList.getDiyPackage());
//    }
//
//
//    public AdminLikedListDto rejectPackage(Long listNum) {
//        DiyList diyList = adminLikedListRepository.findById(listNum)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid package ID: " + listNum));
//
//        diyList.getDiyPackage().setPackageApproval(false);
//        DiyList updatedDiyList = adminLikedListRepository.save(diyList);
//        return new AdminLikedListDto(updatedDiyList.getDiyPackage());
//    }
//}
