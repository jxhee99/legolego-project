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
  public Long createDiy(RequestDTO requestDTO) {
    //airline, route, detailcourse dto를 엔티티로 변환 후 레파지토리에 저장
    AirlineEntity airlineEntity = requestDTO.getAirline().toEntity();
    airlineRepository.save(airlineEntity);

    RouteEntity routeEntity = requestDTO.getRoute().toEntity();
    routeRepository.save(routeEntity);

    saveDetailCourses(requestDTO.getDetailCourses(), routeEntity);

    // requestDTO의 detailCourses 리스트를 순회하면서 fileUrls에서 처음 만나는 null이 아닌 값을 찾기
    String firstUrl = requestDTO.getDetailCourses().stream()
            .map(detailCourse -> detailCourse.getFileUrls().stream()
                    .filter(url -> url != null && !url.isEmpty())
                    .findFirst())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("이미지 최소 한개 필요"));


    User user = userRepository.findById(requestDTO.getUserNum())
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

    //diyEntity 생성 후 저장
    DiyPackage diyPackage = DiyPackage.builder()
            .packageName(requestDTO.getPackageForm().getPackageName())
            .profileImg(firstUrl)
            .shortDescription(requestDTO.getPackageForm().getShortDescription())
            .regDate(LocalDate.now())
            .airline(airlineEntity)
            .route(routeEntity)
            .user(user)
            .packageApproval(false)
            .build();

    DiyPackage savedDiyPackage = diyRepository.save(diyPackage);
    return savedDiyPackage.getPackageNum(); // 저장된 패키지 번호 반환
  }
  //전체조회
  public List<DiyPackage> getDiyPackages(){
    //최신등록순으로 반환(등록일에 날짜만 받고 있어서...packageNum으로 내림차순)
    return diyRepository.findAllByOrderByPackageNumDesc();
  }

  //상세조회
  public ResponseDTO getDiyDetail(Long packageNum, Long currentUserNum) {
    //조회수 증가
    diyRepository.incrementViewNum(packageNum);

    //패키지 조회
    DiyPackage diyPackage = diyRepository.findById(packageNum)
            .orElseThrow(() -> new  IllegalArgumentException("패키지를 찾을 수 없습니다"));

    //로그인한 사용자가 가수요 참여했는 지 검사
    boolean isLiked = diyLikeRepository.existsByUserNumAndDiy(currentUserNum, diyPackage);

    //작성자 인지 검사
    boolean isWriter = diyPackage.getUser().getUserNum() == currentUserNum;


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

    updateAirline(diyPackage.getAirline(), requestDTO.getAirline());
    updateRoute(diyPackage.getRoute(), requestDTO.getRoute());

    detailCourseRepository.deleteByRoute(diyPackage.getRoute());
    saveDetailCourses(requestDTO.getDetailCourses(), diyPackage.getRoute());

    updateDiyEntity(diyPackage, requestDTO.getPackageForm());
    return diyRepository.save(diyPackage);
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



  //put update관련 메서드
  private void updateAirline(AirlineEntity airlineEntity, DiyAirlineDTO diyAirlineDTO) {
    BeanUtils.copyProperties(diyAirlineDTO, airlineEntity);
    airlineRepository.save(airlineEntity);
  }

  private void updateRoute(RouteEntity routeEntity, DiyRouteDTO diyRouteDTO) {
    BeanUtils.copyProperties(diyRouteDTO, routeEntity);
    routeRepository.save(routeEntity);
  }
  private void updateDiyEntity(DiyPackage diyPackage, DiyDTO diyDTO) {
    BeanUtils.copyProperties(diyDTO, diyPackage);
    diyRepository.save(diyPackage);
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