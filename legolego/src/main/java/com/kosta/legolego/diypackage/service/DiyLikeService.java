package com.kosta.legolego.diypackage.service;

import com.kosta.legolego.diypackage.dto.DiyLikeDTO;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.entity.DiyLikeEntity;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.diypackage.repository.DiyLikeRepository;
import com.kosta.legolego.diypackage.repository.DiyRepository;
import com.kosta.legolego.diypackage.repository.OverLikedListRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiyLikeService {
  @Autowired
  private DiyRepository diyRepository;

  @Autowired
  private DiyLikeRepository diyLikeRepository;

  @Autowired
  private OverLikedListRepository overLikedListRepository;

  @Transactional
  public DiyLikeEntity diyLike(DiyLikeDTO diyLikeDTO) {
    Long packageNum = diyLikeDTO.getPackageNum();
    // DIY 패키지 조회
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new RuntimeException("해당 패키지를 찾을 수 없습니다."));

    // 작성자가 자신의 DIY 패키지에 응원할 수 없음
    Long currentUserNum = diyLikeDTO.getUserNum();
    Long packageOwnerUserNum = diyPackage.getUser().getUserNum();
    if (currentUserNum.equals(packageOwnerUserNum)) {
      throw new RuntimeException("작성자는 응원에 참여할 수 없습니다.");
    }
    // 같은 사람이 중복해서 응원할 수 없음
    DiyLikeEntity existingLike = diyLikeRepository.findByUserNumAndDiy(currentUserNum, diyPackage);
    if (existingLike != null) {
      throw new RuntimeException("중복해서 응원할 수 없습니다.");
    }
    // DiyLikeEntity 생성
    DiyLikeEntity diyLikeEntity = diyLikeDTO.toEntity(diyPackage);
    // diy패키지에 응원하기(좋아요 수)증가
    int currentLikedNum = diyPackage.getPackageLikedNum();
    diyPackage.setPackageLikedNum(currentLikedNum + 1);

    //저장
    diyRepository.save(diyPackage);
    diyLikeRepository.save(diyLikeEntity);

    // 좋아요 수가 25에 도달했을 경우 OverLikedList에 추가,  테스트용으로 2개로 설정
    if (diyPackage.getPackageLikedNum() == 2) {
      OverLikedList overLikedList = new OverLikedList();
      overLikedList.setDiyPackage(diyPackage);
      overLikedListRepository.save(overLikedList);

    }

    return diyLikeEntity;
  }
  @Transactional
  public void likeCancel(DiyLikeDTO diyLikeDTO){
    Long packageNum = diyLikeDTO.getPackageNum();
    Long currentUserNum = diyLikeDTO.getUserNum();
    // DIY 패키지 조회
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new RuntimeException("해당 패키지를 찾을 수 없습니다."));
    //사용자, diy패키지가 일치하는 엔티티 조회
    DiyLikeEntity diyLikeEntity = diyLikeRepository.findByUserNumAndDiy(currentUserNum, diyPackage);
    if (diyLikeEntity == null) {
      throw new RuntimeException("응원에 참여한적 없는 패키지입니다.");
    }
    diyLikeRepository.delete(diyLikeEntity);

    // diy패키지에 응원하기(좋아요 수) 감소
    int currentLikedNum = diyPackage.getPackageLikedNum();
    diyPackage.setPackageLikedNum(currentLikedNum - 1);
    //저장
    diyRepository.save(diyPackage);
  }
}
