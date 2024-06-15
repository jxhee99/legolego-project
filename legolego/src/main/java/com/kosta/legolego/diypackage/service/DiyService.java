package com.kosta.legolego.diypackage.service;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.*;
import com.kosta.legolego.diypackage.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Transactional
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

  //diy 생성
  public DiyEntity createDiy(RequestDTO requestDTO) {
    //airline, route, detailcourse dto를 엔티티로 변환 후 레파지토리에 저장
    AirlineEntity airlineEntity = requestDTO.getAirline().toEntity();
    airlineRepository.save(airlineEntity);

    RouteEntity routeEntity = requestDTO.getRoute().toEntity();
    routeRepository.save(routeEntity);

    saveDetailCourses(requestDTO.getDetailCourses(), routeEntity);

    //diyEntity 생성 후 저장
    DiyEntity diyEntity = DiyEntity.builder()
            .packageName(requestDTO.getPackageForm().getPackageName())
            .profileImg(requestDTO.getPackageForm().getProfileImg())
            .shortDescription(requestDTO.getPackageForm().getShortDescription())
            .regDate(LocalDate.now())
            .airline(airlineEntity)
            .route(routeEntity)
            .userNum(requestDTO.getUserNum())
            .build();

    return diyRepository.save(diyEntity);
  }
  //전체조회
  public List<DiyEntity> getDiyPackages(){
    return diyRepository.findAll();
  }

  //상세조회
  public ResponseDTO getDiyDetail(Long packageNum, Long currentUserNum) {
    //조회수 증가
    diyRepository.incrementViewNum(packageNum);

    //패키지 조회
    DiyEntity diyEntity = diyRepository.findById(packageNum)
            .orElseThrow(() -> new NullPointerException("패키지를 찾을 수 없습니다"));

    //로그인한 사용자가 가수요 참여했는 지 검사
    boolean isLiked = diyLikeRepository.existsByUserNumAndDiy(currentUserNum, diyEntity);

    //엔티티를 dto로 변환
    DiyAirlineDTO diyAirlineDTO = DiyAirlineDTO.toAirlineDTO(diyEntity.getAirline());
    DiyRouteDTO diyRouteDTO = DiyRouteDTO.toRouteDTO(diyEntity.getRoute());
    List<DiyDetailCourseDTO> diyDetailCourseDTOList = DiyDetailCourseDTO.toDetailCourseDTOList(
            detailCourseRepository.findByRoute(diyEntity.getRoute())
    );
    DiyDTO diyDTO = DiyDTO.toDiyDTO(diyEntity);

    //ResponseDTO 형태로 반환
    return ResponseDTO.builder()
            .airline(diyAirlineDTO)
            .route(diyRouteDTO)
            .packageForm(diyDTO)
            .detailCourses(diyDetailCourseDTOList)
            .userNum(diyEntity.getUserNum())
            .likedNum(diyEntity.getPackageLikedNum())
            .viewNum(diyEntity.getPackageViewNum())
            .isLiked(isLiked)
            .build();
  }
//put 수정
  public DiyEntity updateDiy(Long packageNum, RequestDTO requestDTO) {
    DiyEntity diyEntity = diyRepository.findById(packageNum)
            .orElseThrow(() -> new NullPointerException("패키지를 찾을 수 없습니다"));

    updateAirline(diyEntity.getAirline(), requestDTO.getAirline());
    updateRoute(diyEntity.getRoute(), requestDTO.getRoute());

    detailCourseRepository.deleteByRoute(diyEntity.getRoute());
    saveDetailCourses(requestDTO.getDetailCourses(), diyEntity.getRoute());

    updateDiyEntity(diyEntity, requestDTO.getPackageForm());
    return diyRepository.save(diyEntity);
  }
//patch 수정
  public DiyEntity updateDiyPatch(Long packageNum, RequestDTO requestDTO) {
    DiyEntity diyEntity = diyRepository.findById(packageNum)
            .orElseThrow(() -> new NullPointerException("패키지를 찾을 수 없습니다"));

    updatePartialAirline(diyEntity.getAirline(), requestDTO.getAirline());
    updatePartialRoute(diyEntity.getRoute(), requestDTO.getRoute());
    updatePartialCourse(diyEntity.getRoute(), requestDTO.getDetailCourses());
    updatePartialDiyEntity(diyEntity, requestDTO.getPackageForm());

    return diyRepository.save(diyEntity);
  }
//삭제
  public void deleteDiy(Long packageNum){
    DiyEntity diyEntity = diyRepository.findById(packageNum)
            .orElseThrow(() -> new NullPointerException("패키지를 찾을 수 없습니다"));

    List<DiyLikeEntity> diyLikes = diyLikeRepository.findByDiy(diyEntity);
    diyLikeRepository.deleteAll(diyLikes);
    diyRepository.delete(diyEntity);
    airlineRepository.delete(diyEntity.getAirline());
    routeRepository.delete(diyEntity.getRoute());
    detailCourseRepository.deleteByRoute(diyEntity.getRoute());
  }


  private void saveDetailCourses(List<DiyDetailCourseDTO> diyDetailCourseDTOS, RouteEntity routeEntity) {
    for (DiyDetailCourseDTO diyDetailCourseDTO : diyDetailCourseDTOS) {
      DetailCourseEntity detailCourseEntity = diyDetailCourseDTO.toEntity(routeEntity);
      detailCourseRepository.save(detailCourseEntity);
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
  private void updateDiyEntity(DiyEntity diyEntity, DiyDTO diyDTO) {
    BeanUtils.copyProperties(diyDTO, diyEntity);
    diyRepository.save(diyEntity);
  }

  //patch update 관련 메서드
  private void updatePartialAirline(AirlineEntity airlineEntity, DiyAirlineDTO diyAirlineDTO) {
    if (diyAirlineDTO != null) {
      Optional.ofNullable(diyAirlineDTO.getAirlineName()).ifPresent(airlineEntity::setAirlineName);
      Optional.ofNullable(diyAirlineDTO.getStartFlightNum()).ifPresent(airlineEntity::setStartFlightNum);
      Optional.ofNullable(diyAirlineDTO.getStartingPoint()).ifPresent(airlineEntity::setStartingPoint);
      Optional.ofNullable(diyAirlineDTO.getDestination()).ifPresent(airlineEntity::setDestination);
      Optional.ofNullable(diyAirlineDTO.getBoardingDate()).ifPresent(airlineEntity::setBoardingDate);
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
    Optional.ofNullable(dto.getDayNum()).ifPresent(entity::setDayNum);

    List<String> courses = dto.getCourses();
    //필드 갯수 만큼 코스 설정
    if (courses != null && !courses.isEmpty()) {
      entity.clearCourses(); // 모든 코스 필드를 초기화
      for (int i = 0; i < Math.min(courses.size(), 10); i++) {
        entity.setCourse(i + 1, courses.get(i));
      }
    }
    Optional.ofNullable(dto.getFileUrl()).ifPresent(entity::setFileUrl);
  }
  private void updatePartialDiyEntity(DiyEntity diyEntity, DiyDTO diyDTO) {
    if (diyDTO != null) {
      Optional.ofNullable(diyDTO.getPackageName()).ifPresent(diyEntity::setPackageName);
      Optional.ofNullable(diyDTO.getProfileImg()).ifPresent(diyEntity::setProfileImg);
      Optional.ofNullable(diyDTO.getShortDescription()).ifPresent(diyEntity::setShortDescription);
      diyRepository.save(diyEntity);
    }
  }
}