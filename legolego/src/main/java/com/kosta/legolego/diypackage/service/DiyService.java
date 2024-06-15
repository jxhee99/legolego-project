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
    AirlineEntity airlineEntity = saveAirline(requestDTO.getAirline());
    RouteEntity routeEntity = saveRoute(requestDTO.getRoute());
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

    DiyEntity diyEntity = diyRepository.findById(packageNum)
            .orElseThrow(() -> new NullPointerException("패키지를 찾을 수 없습니다"));

    //로그인한 유저가 응원하기 참여했는지 확인
    boolean isLiked = diyLikeRepository.existsByUserNumAndDiy(currentUserNum, diyEntity);

    AirlineDTO airlineDTO = toAirlineDTO(diyEntity.getAirline());
    RouteDTO routeDTO = toRouteDTO(diyEntity.getRoute());
    List<DetailCourseDTO> detailCourseDTOList = toDetailCourseDTOList(diyEntity.getRoute());
    PackageFormDTO packageFormDTO = PackageFormDTO.getDiyEntity(diyEntity);

    return ResponseDTO.builder()
            .airline(airlineDTO)
            .route(routeDTO)
            .packageForm(packageFormDTO)
            .detailCourses(detailCourseDTOList)
            .userNum(diyEntity.getUserNum())
            .likedNum(diyEntity.getPackageLikedNum())
            .viewNum(diyEntity.getPackageViewNum())
            .isLiked(isLiked)
            .build();
  }

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
public DiyEntity updateDiyPatch(Long packageNum, RequestDTO requestDTO) {
  DiyEntity diyEntity = diyRepository.findById(packageNum)
          .orElseThrow(() -> new NullPointerException("패키지를 찾을 수 없습니다"));

  updatePartialAirline(diyEntity.getAirline(), requestDTO.getAirline());
  updatePartialRoute(diyEntity.getRoute(), requestDTO.getRoute());
  updateDetailCourses(diyEntity.getRoute(), requestDTO.getDetailCourses());
  updatePartialDiyEntity(diyEntity, requestDTO.getPackageForm());

  //DetailCourse 업데이트(삭제 후 새로 저장 방식: 밑의 방법이 예상치 못한 버그가 많으면 적용
//  List<DetailCourseDTO> detailCourseDTOs = requestDTO.getDetailCourses();
//  if (detailCourseDTOs != null && !detailCourseDTOs.isEmpty()) {
//    // 기존의 DetailCourse 삭제 후 새로 저장
//    detailCourseRepository.deleteByRoute(routeEntity);
//    for (DetailCourseDTO detailCourseDTO : detailCourseDTOs) {
//      //detailCourseDTO.setRouteNum(routeEntity.getRouteNum());
//      DetailCourseEntity detailCourseEntity = detailCourseDTO.toEntity(routeEntity);
//      detailCourseRepository.save(detailCourseEntity);
//    }
//  }
  return diyRepository.save(diyEntity);
}

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

  private AirlineEntity saveAirline(AirlineDTO airlineDTO) {
    AirlineEntity airlineEntity = airlineDTO.toEntity();
    return airlineRepository.save(airlineEntity);
  }

  private RouteEntity saveRoute(RouteDTO routeDTO) {
    RouteEntity routeEntity = routeDTO.toEntity();
    return routeRepository.save(routeEntity);
  }

  private void saveDetailCourses(List<DetailCourseDTO> detailCourseDTOs, RouteEntity routeEntity) {
    for (DetailCourseDTO detailCourseDTO : detailCourseDTOs) {
      DetailCourseEntity detailCourseEntity = detailCourseDTO.toEntity(routeEntity);
      detailCourseRepository.save(detailCourseEntity);
    }
  }

  private AirlineDTO toAirlineDTO(AirlineEntity airlineEntity) {
    AirlineDTO airlineDTO = new AirlineDTO();
    BeanUtils.copyProperties(airlineEntity, airlineDTO);
    return airlineDTO;
  }

  private RouteDTO toRouteDTO(RouteEntity routeEntity) {
    RouteDTO routeDTO = new RouteDTO();
    BeanUtils.copyProperties(routeEntity, routeDTO);
    return routeDTO;
  }

  private List<DetailCourseDTO> toDetailCourseDTOList(RouteEntity routeEntity) {
    List<DetailCourseEntity> detailCourseEntities = detailCourseRepository.findByRoute(routeEntity);
    return detailCourseEntities.stream()
            .map(detailCourse ->DetailCourseDTO.builder()
                .detailCourseNum(detailCourse.getDetailCourseNum())
                .dayNum(detailCourse.getDayNum())
                .courses(Arrays.asList(
                        detailCourse.getCourse1(),
                        detailCourse.getCourse2(),
                        detailCourse.getCourse3(),
                        detailCourse.getCourse4(),
                        detailCourse.getCourse5(),
                        detailCourse.getCourse6(),
                        detailCourse.getCourse7(),
                        detailCourse.getCourse8(),
                        detailCourse.getCourse9(),
                        detailCourse.getCourse10()
                ).stream().filter(Objects::nonNull).collect(Collectors.toList()))
                .fileUrl(detailCourse.getFileUrl())
                .build())
            .collect(Collectors.toList());
  }
  private void updateAirline(AirlineEntity airlineEntity, AirlineDTO airlineDTO) {
    BeanUtils.copyProperties(airlineDTO, airlineEntity);
    airlineRepository.save(airlineEntity);
  }

  private void updateRoute(RouteEntity routeEntity, RouteDTO routeDTO) {
    BeanUtils.copyProperties(routeDTO, routeEntity);
    routeRepository.save(routeEntity);
  }

  private void updateDetailCourses(RouteEntity routeEntity, List<DetailCourseDTO> detailCourseDTOs) {
    if(detailCourseDTOs != null){
      List<DetailCourseEntity> existingCourses = detailCourseRepository.findByRoute(routeEntity);
      Map<Long, DetailCourseEntity> existingCourseMap = existingCourses.stream()
              .collect(Collectors.toMap(DetailCourseEntity::getDetailCourseNum, Function.identity()));

      for (DetailCourseDTO detailCourseDTO : detailCourseDTOs) {
        if (detailCourseDTO.getDetailCourseNum() != null && existingCourseMap.containsKey(detailCourseDTO.getDetailCourseNum())) {
          DetailCourseEntity existingCourse = existingCourseMap.get(detailCourseDTO.getDetailCourseNum());
          updateDetailCourseEntity(existingCourse, detailCourseDTO);
          detailCourseRepository.save(existingCourse);
          existingCourseMap.remove(detailCourseDTO.getDetailCourseNum());
        } else {
          // 새로운 엔티티 추가
          DetailCourseEntity newCourse = detailCourseDTO.toEntity(routeEntity);
          detailCourseRepository.save(newCourse);
        }
      }

      // 남은 기존 엔티티 삭제
      for (DetailCourseEntity remainingCourse : existingCourseMap.values()) {
        detailCourseRepository.delete(remainingCourse);
      }
    }
  }

  private void updatePartialAirline(AirlineEntity airlineEntity, AirlineDTO airlineDTO) {
    if (airlineDTO != null) {
      if (airlineDTO.getAirlineName() != null) {
        airlineEntity.setAirlineName(airlineDTO.getAirlineName());
      }
      if (airlineDTO.getStartFlightNum() != null) {
        airlineEntity.setStartFlightNum(airlineDTO.getStartFlightNum());
      }
      if (airlineDTO.getStartingPoint() != null) {
        airlineEntity.setStartingPoint(airlineDTO.getStartingPoint());
      }
      if (airlineDTO.getDestination() != null) {
        airlineEntity.setDestination(airlineDTO.getDestination());
      }
      if (airlineDTO.getBoardingDate() != null) {
        airlineEntity.setBoardingDate(airlineDTO.getBoardingDate());
      }
      if (airlineDTO.getComeFlightNum() != null) {
        airlineEntity.setComeFlightNum(airlineDTO.getComeFlightNum());
      }
      if (airlineDTO.getComingDate() != null) {
        airlineEntity.setComingDate(airlineDTO.getComingDate());
      }
      airlineRepository.save(airlineEntity);
    }
  }

  private void updatePartialRoute(RouteEntity routeEntity, RouteDTO routeDTO) {
    if (routeDTO != null) {
      if (routeDTO.getStartDate() != null) {
        routeEntity.setStartDate(routeDTO.getStartDate());
      }
      if (routeDTO.getLastDate() != null) {
        routeEntity.setLastDate(routeDTO.getLastDate());
      }
      routeRepository.save(routeEntity);
    }
  }

  private void updatePartialDiyEntity(DiyEntity diyEntity, PackageFormDTO packageFormDTO) {
    if (packageFormDTO != null) {
      if (packageFormDTO.getPackageName() != null) {
        diyEntity.setPackageName(packageFormDTO.getPackageName());
      }
      if (packageFormDTO.getProfileImg() != null) {
        diyEntity.setProfileImg(packageFormDTO.getProfileImg());
      }
      if (packageFormDTO.getShortDescription() != null) {
        diyEntity.setShortDescription(packageFormDTO.getShortDescription());
      }
      diyRepository.save(diyEntity);
    }
  }

  private void updateDiyEntity(DiyEntity diyEntity, PackageFormDTO packageFormDTO) {
    BeanUtils.copyProperties(packageFormDTO, diyEntity);
    diyRepository.save(diyEntity);
  }
  private void updateDetailCourseEntity(DetailCourseEntity entity, DetailCourseDTO dto) {
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
}