package com.kosta.legolego.user.service;

import com.kosta.legolego.user.dto.MyPageDto;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.user.repository.MyPageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyPageService {

    @Autowired
    private MyPageRepository myPageRepository;

    // 내가 쓴 글 리스트 조회
//    public List<MyPageDto> getUserMyPage(Long userNum) {
////        List<MyPage> myPages = myPageRepository.findByUser_userNum(userNum);
////        return myPages.stream().map(MyPageDto::new).collect(Collectors.toList());
////        return myPageRepository.findByUserUserNum(userNum).stream()
////                .map(MyPageDto::new)
////                .collect(Collectors.toList());
//
//        log.info("Fetching pages for userNum: {}", userNum);
//
//        List<MyPage> myPages = myPageRepository.findByUserUserNum(userNum);
//        log.info("Fetched MyPage entities: {}", myPages);
//
//        List<MyPageDto> myPageDtos = myPageRepository.findByUserUserNum(userNum).stream()
//                .map(MyPageDto::new)
//                .collect(Collectors.toList());
//
//        myPageDtos.forEach(dto -> log.info("Fetched MyPageDto: {}", dto));
//
//        return myPageDtos;

//    }

    // 내가 쓴 글 목록 중 응원하기 조건 충족한 리스트만 조회
    public List<MyPageDto> getUserLikedPackages(Long userNum, Integer packageLikedNum) {
        log.info("Fetching liked packages for userNum: {} with packageLikedNum >= {}", userNum, packageLikedNum);
        List<DiyList> diyLists = myPageRepository.findByDiyPackageUserUserNumAndDiyPackagePackageLikedNumGreaterThanEqual(userNum, packageLikedNum);
        return diyLists.stream().map(MyPageDto::new).collect(Collectors.toList());
    }
}
