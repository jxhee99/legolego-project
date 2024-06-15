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
      if (diyAirlineDTO.getAirlineName() != null) {
        airlineEntity.setAirlineName(diyAirlineDTO.getAirlineName());
      }
      if (diyAirlineDTO.getStartFlightNum() != null) {
        airlineEntity.setStartFlightNum(diyAirlineDTO.getStartFlightNum());
      }
      if (diyAirlineDTO.getStartingPoint() != null) {
        airlineEntity.setStartingPoint(diyAirlineDTO.getStartingPoint());
      }
      if (diyAirlineDTO.getDestination() != null) {
        airlineEntity.setDestination(diyAirlineDTO.getDestination());
      }
      if (diyAirlineDTO.getBoardingDate() != null) {
        airlineEntity.setBoardingDate(diyAirlineDTO.getBoardingDate());
      }
      if (diyAirlineDTO.getComeFlightNum() != null) {
        airlineEntity.setComeFlightNum(diyAirlineDTO.getComeFlightNum());
      }
      if (diyAirlineDTO.getComingDate() != null) {
        airlineEntity.setComingDate(diyAirlineDTO.getComingDate());
      }
      airlineRepository.save(airlineEntity);
    }
  }

  private void updatePartialRoute(RouteEntity routeEntity, DiyRouteDTO diyRouteDTO) {
    if (diyRouteDTO != null) {
      if (diyRouteDTO.getStartDate() != null) {
        routeEntity.setStartDate(diyRouteDTO.getStartDate());
      }
      if (diyRouteDTO.getLastDate() != null) {
        routeEntity.setLastDate(diyRouteDTO.getLastDate());
      }
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
    if (dto.getCourses() != null && !dto.getCourses().isEmpty()) {
      int size = dto.getCourses().size();
      entity.setCourse1(size > 0 ? dto.getCourses().get(0) : null);
      entity.setCourse2(size > 1 ? dto.getCourses().get(1) : null);
      entity.setCourse3(size > 2 ? dto.getCourses().get(2) : null);
      entity.setCourse4(size > 3 ? dto.getCourses().get(3) : null);
      entity.setCourse5(size > 4 ? dto.getCourses().get(4) : null);
      entity.setCourse6(size > 5 ? dto.getCourses().get(5) : null);
      entity.setCourse7(size > 6 ? dto.getCourses().get(6) : null);
      entity.setCourse8(size > 7 ? dto.getCourses().get(7) : null);
      entity.setCourse9(size > 8 ? dto.getCourses().get(8) : null);
      entity.setCourse10(size > 9 ? dto.getCourses().get(9) : null);
    }
    if (dto.getFileUrl() != null) {
      entity.setFileUrl(dto.getFileUrl());
    }
  }
  private void updatePartialDiyEntity(DiyEntity diyEntity, DiyDTO diyDTO) {
    if (diyDTO != null) {
      if (diyDTO.getPackageName() != null) {
        diyEntity.setPackageName(diyDTO.getPackageName());
      }
      if (diyDTO.getProfileImg() != null) {
        diyEntity.setProfileImg(diyDTO.getProfileImg());
      }
      if (diyDTO.getShortDescription() != null) {
        diyEntity.setShortDescription(diyDTO.getShortDescription());
      }
      diyRepository.save(diyEntity);
    }
  }

}