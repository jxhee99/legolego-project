package com.kosta.legolego.diypackage.service;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.*;
import com.kosta.legolego.diypackage.repository.*;
import com.kosta.legolego.image.repository.ImageRepository;
import com.kosta.legolego.image.service.ImageService;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Transactional
@Slf4j
@Service
public class DiyService {
  @Autowired
  private DiyRepository diyRepository;
  @Autowired
  private DiyLikeRepository diyLikeRepository;
  @Autowired
  private AirlineRepository airlineRepository;
  @Autowired
  private RouteRepository routeRepository;
  @Autowired
  private DetailCourseRepository detailCourseRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private OverLikedListRepository overLikedListRepository;
  @Autowired
  private ImageService imageService;
  @Autowired
  private ImageRepository imageRepository;

  //diy 생성
  public Long saveDraft(RequestDTO requestDTO) {
    return saveOrUpdateDiy(requestDTO, true);
  }

  public Long createDiy(RequestDTO requestDTO) {
    return saveOrUpdateDiy(requestDTO, false);
  }

  private Long saveOrUpdateDiy(RequestDTO requestDTO, boolean isDraft) {
    User user = userRepository.findById(requestDTO.getUserNum())
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));

    DiyPackage diyPackage = diyRepository.findFirstByUserUserNumAndPackageDraftTrue(requestDTO.getUserNum());

    AirlineEntity airlineEntity;
    RouteEntity routeEntity;

    if (diyPackage != null && isDraft) {
      // 기존 임시 저장된 패키지가 있을 경우 해당 패키지의 airline과 route를 업데이트
      airlineEntity = diyPackage.getAirline();
      updateAirlineEntity(airlineEntity, requestDTO.getAirline());
      airlineRepository.save(airlineEntity);

      routeEntity = diyPackage.getRoute();
      updateRouteEntity(routeEntity, requestDTO.getRoute());
      routeRepository.save(routeEntity);
    } else {
      // 새로운 airline과 route 생성
      airlineEntity = requestDTO.getAirline().toEntity();
      airlineRepository.save(airlineEntity);

      routeEntity = requestDTO.getRoute().toEntity();
      routeRepository.save(routeEntity);
    }

    saveDetailCourses(requestDTO.getDetailCourses(), routeEntity);

    String firstUrl = requestDTO.getDetailCourses().stream()
            .map(detailCourse -> detailCourse.getFileUrls().stream()
                    .filter(url -> url != null && !url.isEmpty())
                    .findFirst())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElse(null);

    if (diyPackage != null && isDraft) {
      // 기존 임시 저장된 패키지를 업데이트
      diyPackage.setPackageName(requestDTO.getPackageForm() != null ? requestDTO.getPackageForm().getPackageName() : null);
      diyPackage.setProfileImg(firstUrl);
      diyPackage.setShortDescription(requestDTO.getPackageForm() != null ? requestDTO.getPackageForm().getShortDescription() : null);
      diyPackage.setModDate(LocalDate.now());
      diyPackage.setAirline(airlineEntity);
      diyPackage.setRoute(routeEntity);
      diyPackage.setPackageDraft(true); // 임시 저장 상태 유지
    } else if (isDraft) {
      // 새 임시 저장 패키지 생성
      diyPackage = DiyPackage.builder()
              .packageName(requestDTO.getPackageForm() != null ? requestDTO.getPackageForm().getPackageName() : null)
              .profileImg(firstUrl)
              .shortDescription(requestDTO.getPackageForm() != null ? requestDTO.getPackageForm().getShortDescription() : null)
              .regDate(LocalDate.now())
              .modDate(LocalDate.now())
              .airline(airlineEntity)
              .route(routeEntity)
              .user(user)
              .packageDraft(true) // 임시 저장 상태 설정
              .build();
    } else {
      // 최종 저장 시: 모든 필드가 채워져 있는지 확인
      if (requestDTO.getPackageForm() == null || requestDTO.getPackageForm().getPackageName() == null || requestDTO.getPackageForm().getShortDescription() == null || firstUrl == null) {
        throw new IllegalArgumentException("패키지 이름, 짧은 설명이필요합니다.");
      }

      if (diyPackage != null) {
        // 기존 임시 저장된 패키지를 최종 저장으로 업데이트
        diyPackage.setPackageName(requestDTO.getPackageForm().getPackageName());
        diyPackage.setProfileImg(firstUrl);
        diyPackage.setShortDescription(requestDTO.getPackageForm().getShortDescription());
        diyPackage.setModDate(LocalDate.now());
        diyPackage.setAirline(airlineEntity);
        diyPackage.setRoute(routeEntity);
        diyPackage.setPackageDraft(false); // 최종 저장 상태로 변경
      } else {
        // 새 패키지 생성
        diyPackage = DiyPackage.builder()
                .packageName(requestDTO.getPackageForm().getPackageName())
                .profileImg(firstUrl)
                .shortDescription(requestDTO.getPackageForm().getShortDescription())
                .regDate(LocalDate.now())
                .modDate(LocalDate.now())
                .airline(airlineEntity)
                .route(routeEntity)
                .user(user)
                .packageDraft(false) // 최종 저장 상태 설정
                .build();
      }
    }

    DiyPackage savedDiyPackage = diyRepository.save(diyPackage);
    return savedDiyPackage.getPackageNum();
  }

  private void updateAirlineEntity(AirlineEntity airlineEntity, DiyAirlineDTO airlineDTO) {
    airlineEntity.setStartAirlineName(airlineDTO.getStartAirlineName());
    airlineEntity.setStartingPoint(airlineDTO.getStartingPoint());
    airlineEntity.setDestination(airlineDTO.getDestination());
    airlineEntity.setStartFlightNum(airlineDTO.getStartFlightNum());
    airlineEntity.setBoardingDate(airlineDTO.getBoardingDate());
    airlineEntity.setComeAirlineName(airlineDTO.getComeAirlineName());
    airlineEntity.setComeFlightNum(airlineDTO.getComeFlightNum());
    airlineEntity.setComingDate(airlineDTO.getComingDate());
  }

  private void updateRouteEntity(RouteEntity routeEntity, DiyRouteDTO routeDTO) {
    routeEntity.setStartDate(routeDTO.getStartDate());
    routeEntity.setLastDate(routeDTO.getLastDate());
  }

  public Long finalizeDiy(Long packageNum, Long userNum, RequestDTO requestDTO) {
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new IllegalArgumentException("해당 패키지를 찾을 수 없습니다"));

    // 업데이트 로직 추가
    if (requestDTO.getPackageForm() == null || requestDTO.getPackageForm().getPackageName() == null || requestDTO.getPackageForm().getShortDescription() == null || requestDTO.getPackageForm().getProfileImg() == null) {
      throw new IllegalArgumentException("패키지 이름, 짧은 설명이필요합니다.");
    }

    diyPackage.setPackageName(requestDTO.getPackageForm().getPackageName());
    diyPackage.setProfileImg(requestDTO.getPackageForm().getProfileImg());
    diyPackage.setShortDescription(requestDTO.getPackageForm().getShortDescription());
    diyPackage.setModDate(LocalDate.now());
    diyPackage.setPackageDraft(false); // 최종 저장 상태로 변경

    RouteEntity routeEntity = requestDTO.getRoute().toEntity();
    routeRepository.save(routeEntity);
    diyPackage.setRoute(routeEntity);

    DiyPackage savedDiyPackage = diyRepository.save(diyPackage);
    return savedDiyPackage.getPackageNum();
  }

  //전체조회
  public List<DiyPackage> getDiyPackages(){
    //최신등록순으로 반환(등록일에 날짜만 받고 있어서...packageNum으로 내림차순)
    return diyRepository.findAllByPackageDraftFalseOrderByPackageNumDesc();
  }

  //상세조회
  public ResponseDTO getDiyDetail(Long packageNum, Long currentUserNum) {
    //조회수 증가
    diyRepository.incrementViewNum(packageNum);

    //패키지 조회
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new  IllegalArgumentException("패키지를 찾을 수 없습니다"));

    // 로그인한 사용자가 가수요 참여했는 지 검사
    boolean isLiked = (currentUserNum != null) && diyLikeRepository.existsByUserNumAndDiy(currentUserNum, diyPackage);

    // 작성자인지 검사
    boolean isWriter = (currentUserNum != null) && (diyPackage.getUser().getUserNum() ==currentUserNum);


    //엔티티를 dto로 변환
    DiyAirlineDTO diyAirlineDTO = DiyAirlineDTO.toAirlineDTO(diyPackage.getAirline());
    DiyRouteDTO diyRouteDTO = DiyRouteDTO.toRouteDTO(diyPackage.getRoute());
    List<DiyDetailCourseDTO> diyDetailCourseDTOList = DiyDetailCourseDTO.toDetailCourseDTOList(
            detailCourseRepository.findByRoute(diyPackage.getRoute())
    );
    // 각 DiyDetailCourseDTO에 이미지 URL 리스트를 설정
    for (DiyDetailCourseDTO diyDetailCourseDTO : diyDetailCourseDTOList) {
      Long detailCourseNum = diyDetailCourseDTO.getDetailCourseNum();
      List<String> imageUrls = imageService.getImagesByDetailCourse(detailCourseNum);
      diyDetailCourseDTO.setFileUrls(imageUrls);
    }
    WriterDTO writerDTO = WriterDTO.toWriterDTO(diyPackage);
    DiyDTO diyDTO = DiyDTO.toDiyDTO(diyPackage);

    //ResponseDTO 형태로 반환
    return ResponseDTO.builder()
            .airline(diyAirlineDTO)
            .route(diyRouteDTO)
            .packageForm(diyDTO)
            .user(writerDTO)
            .detailCourses(diyDetailCourseDTOList)
            .likedNum(diyPackage.getPackageLikedNum())
            .viewNum(diyPackage.getPackageViewNum())
            .regDate(diyPackage.getRegDate())
            .isLiked(isLiked)
            .isWriter(isWriter)
            .build();
  }
//put 수정
  public DiyPackage updateDiy(Long packageNum, RequestDTO requestDTO) {
    DiyPackage diyPackage = diyRepository.findById(packageNum)

            .orElseThrow(() -> new  IllegalArgumentException("패키지를 찾을 수 없습니다"));

    // DetailCourseEntity 리스트 가져오기
    List<DetailCourseEntity> detailCourses = detailCourseRepository.findByRoute(diyPackage.getRoute());

    // 각 DetailCourseEntity에 연결된 Image 삭제
    for (DetailCourseEntity detailCourse : detailCourses) {
      imageRepository.deleteByDetailCourse(detailCourse);
    }
    //디테일 코스 삭제 후 새로 저장
    detailCourseRepository.deleteByRoute(diyPackage.getRoute());
    saveDetailCourses(requestDTO.getDetailCourses(), diyPackage.getRoute());

    //썸네일 이미지 설정
    String firstUrl = requestDTO.getDetailCourses().stream()
            .map(detailCourse -> detailCourse.getFileUrls().stream()
                    .filter(url -> url != null && !url.isEmpty())
                    .findFirst())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("이미지 최소 한개 필요"));
    diyPackage.setProfileImg(firstUrl);

    diyPackage.setPackageName(requestDTO.getPackageForm().getPackageName());
    diyPackage.setShortDescription(requestDTO.getPackageForm().getShortDescription());
    return diyRepository.save(diyPackage);
  }

//삭제
  public void deleteDiy(Long packageNum){
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new  IllegalArgumentException("패키지를 찾을 수 없습니다"));

    List<DiyLikeEntity> diyLikes = diyLikeRepository.findByDiy(diyPackage);

    // DetailCourseEntity 리스트 가져오기
    List<DetailCourseEntity> detailCourses = detailCourseRepository.findByRoute(diyPackage.getRoute());

    // 각 DetailCourseEntity에 연결된 Image 삭제
    for (DetailCourseEntity detailCourse : detailCourses) {
      imageRepository.deleteByDetailCourse(detailCourse);
    }

    overLikedListRepository.deleteByDiyPackage(diyPackage);
    diyLikeRepository.deleteAll(diyLikes);
    diyRepository.delete(diyPackage);
    airlineRepository.delete(diyPackage.getAirline());
    routeRepository.delete(diyPackage.getRoute());
    detailCourseRepository.deleteByRoute(diyPackage.getRoute());
  }

  //해당 패키지의 작성자Num 반환
  public Long getPackageOwner(Long packageNum){
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new  IllegalArgumentException("패키지를 찾을 수 없습니다"));
    Long userNum = diyPackage.getUser().getUserNum();
    return userNum;
  }
  //해당 패키지의 likeNum 반환
  public int getLikeNum(Long packageNum){
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new  IllegalArgumentException("패키지를 찾을 수 없습니다"));
    int likeNum = diyPackage.getPackageLikedNum();
    return likeNum;
  }

  private void saveDetailCourses(List<DiyDetailCourseDTO> diyDetailCourseDTOS, RouteEntity routeEntity) {
    for (DiyDetailCourseDTO diyDetailCourseDTO : diyDetailCourseDTOS) {

      DetailCourseEntity detailCourseEntity = diyDetailCourseDTO.toEntity(routeEntity);
      detailCourseEntity = detailCourseRepository.save(detailCourseEntity);

      if (diyDetailCourseDTO.getFileUrls() != null && !diyDetailCourseDTO.getFileUrls().isEmpty()) {
        imageService.saveImageUrls(diyDetailCourseDTO.getFileUrls(), detailCourseEntity.getDetailCourseNum());
      } else {
        log.warn("No Image URLs to save for DetailCourseEntity ID: {}", detailCourseEntity.getDetailCourseNum());
      }
    }
  }

  //patch 수정
  public DiyPackage updateDiyPatch(Long packageNum, RequestDTO requestDTO) {
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new  IllegalArgumentException("패키지를 찾을 수 없습니다"));

    updatePartialAirline(diyPackage.getAirline(), requestDTO.getAirline());
    updatePartialRoute(diyPackage.getRoute(), requestDTO.getRoute());
    updatePartialCourse(diyPackage.getRoute(), requestDTO.getDetailCourses());
    updatePartialDiyEntity(diyPackage, requestDTO.getPackageForm());

    return diyRepository.save(diyPackage);
  }
  
  //patch update 관련 메서드
  private void updatePartialAirline(AirlineEntity airlineEntity, DiyAirlineDTO diyAirlineDTO) {
    if (diyAirlineDTO != null) {
      Optional.ofNullable(diyAirlineDTO.getStartAirlineName()).ifPresent(airlineEntity::setStartAirlineName);
      Optional.ofNullable(diyAirlineDTO.getStartFlightNum()).ifPresent(airlineEntity::setStartFlightNum);
      Optional.ofNullable(diyAirlineDTO.getStartingPoint()).ifPresent(airlineEntity::setStartingPoint);
      Optional.ofNullable(diyAirlineDTO.getDestination()).ifPresent(airlineEntity::setDestination);
      Optional.ofNullable(diyAirlineDTO.getBoardingDate()).ifPresent(airlineEntity::setBoardingDate);
      Optional.ofNullable(diyAirlineDTO.getComeAirlineName()).ifPresent(airlineEntity::setComeAirlineName);
      Optional.ofNullable(diyAirlineDTO.getComeFlightNum()).ifPresent(airlineEntity::setComeFlightNum);
      Optional.ofNullable(diyAirlineDTO.getComingDate()).ifPresent(airlineEntity::setComingDate);

      airlineRepository.save(airlineEntity);
    }
  }

  private void updatePartialRoute(RouteEntity routeEntity, DiyRouteDTO diyRouteDTO) {
    if (diyRouteDTO != null) {
      Optional.ofNullable(diyRouteDTO.getStartDate()).ifPresent(routeEntity::setStartDate);
      Optional.ofNullable(diyRouteDTO.getLastDate()).ifPresent(routeEntity::setLastDate);
      routeRepository.save(routeEntity);
    }
  }

  private void updatePartialCourse(RouteEntity routeEntity, List<DiyDetailCourseDTO> diyDetailCourseDTOS) {
    if(diyDetailCourseDTOS != null){
      List<DetailCourseEntity> existingCourses = detailCourseRepository.findByRoute(routeEntity);
      Map<Long, DetailCourseEntity> existingCourseMap = existingCourses.stream()
              .collect(Collectors.toMap(DetailCourseEntity::getDetailCourseNum, Function.identity()));

      for (DiyDetailCourseDTO diyDetailCourseDTO : diyDetailCourseDTOS) {
        if (diyDetailCourseDTO.getDetailCourseNum() != null && existingCourseMap.containsKey(diyDetailCourseDTO.getDetailCourseNum())) {
          DetailCourseEntity existingCourse = existingCourseMap.get(diyDetailCourseDTO.getDetailCourseNum());
          updateDetailCourseEntity(existingCourse, diyDetailCourseDTO);
          detailCourseRepository.save(existingCourse);
          existingCourseMap.remove(diyDetailCourseDTO.getDetailCourseNum());
        } else {
          // 새로운 엔티티 추가
          DetailCourseEntity newCourse = diyDetailCourseDTO.toEntity(routeEntity);
          detailCourseRepository.save(newCourse);
        }
      }

      // 남은 기존 엔티티 삭제
      for (DetailCourseEntity remainingCourse : existingCourseMap.values()) {
        detailCourseRepository.delete(remainingCourse);
      }
    }
  }
  private void updateDetailCourseEntity(DetailCourseEntity entity, DiyDetailCourseDTO dto) {
    if (dto.getDayNum() != null) {
      entity.setDayNum(dto.getDayNum());
    }

    List<String> courses = dto.getCourses();
    //필드 갯수 만큼 코스 설정
    if (courses != null && !courses.isEmpty()) {
      entity.clearCourses(); // 모든 코스 필드를 초기화
      for (int i = 0; i < Math.min(courses.size(), 10); i++) {
        entity.setCourse(i + 1, courses.get(i));
      }
    }
//    if (dto.getFileUrls() != null) {
//      entity.setFileUrls(dto.getFileUrls());
//    }
  }
  private void updatePartialDiyEntity(DiyPackage diyPackage, DiyDTO diyDTO) {
    if (diyDTO != null) {
      Optional.ofNullable(diyDTO.getPackageName()).ifPresent(diyPackage::setPackageName);
      Optional.ofNullable(diyDTO.getProfileImg()).ifPresent(diyPackage::setProfileImg);
      Optional.ofNullable(diyDTO.getShortDescription()).ifPresent(diyPackage::setShortDescription);
      diyRepository.save(diyPackage);
    }
  }
}