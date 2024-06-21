package com.kosta.legolego.user.service;

import com.kosta.legolego.diypackage.entity.DiyLikeEntity;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.repository.DiyLikeRepository;
import com.kosta.legolego.user.dto.MyPageDto;
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
    @Autowired
    private DiyLikeRepository diyLikeRepository;

    // 내가 쓴 글 리스트 조회
    public List<MyPageDto> getPackagesByUserNum(Long userNum) {
        List<DiyPackage> diyPackages = myPageRepository.findByUserUserNum(userNum);
        return diyPackages.stream()
                        .map(MyPageDto::new)
                        .collect(Collectors.toList());
    }

    // 응원하기 버튼 누른 게시물 리스트 조회
    public List<MyPageDto> getLikedPackagesByUserNum(Long userNum) {
        List<DiyLikeEntity> likes = diyLikeRepository.findByUserNum(userNum);
        return likes.stream()
                    .map(like -> new MyPageDto(like.getDiy()))
                    .collect(Collectors.toList());
    }

    // 내가 쓴 글 목록 중 응원하기 조건 충족한 리스트만 조회
//    public List<MyPageDto> getUserLikedPackages(Long userNum, Integer packageLikedNum) {
//        log.info("Fetching liked packages for userNum: {} with packageLikedNum >= {}", userNum, packageLikedNum);
//        List<DiyList> diyLists = myPageRepository.findByDiyPackageUserUserNumAndDiyPackagePackageLikedNumGreaterThanEqual(userNum, packageLikedNum);
//        return diyLists.stream().map(MyPageDto::new).collect(Collectors.toList());
//    }
}
