package com.kosta.legolego.diypackage.service;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import com.kosta.legolego.diypackage.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

  @Transactional
  public DiyEntity createDiy(RequestDTO requestDTO) {
    AirlineDTO airlineDTO = requestDTO.getAirline();
    RouteDTO routeDTO = requestDTO.getRoute();
    List<DetailCourseDTO> detailCourseDTOs = requestDTO.getDetailCourses();
    PackageFormDTO packageFormDTO = requestDTO.getPackageForm();

    AirlineEntity airlineEntity = airlineDTO.toEntity();
    RouteEntity routeEntity = routeDTO.toEntity();

    airlineEntity = airlineRepository.save(airlineEntity);
    routeEntity = routeRepository.save(routeEntity);

    //for문으로 detailCourseDTO 갯수 만큼 유동적으로 처리
    for (DetailCourseDTO detailCourseDTO : detailCourseDTOs) {
      // RouteEntity를 DetailCourseDTO에 설정
      //detailCourseDTO.setRouteNum(routeEntity.getRouteNum());
      DetailCourseEntity detailCourseEntity = detailCourseDTO.toEntity(routeEntity);
      detailCourseRepository.save(detailCourseEntity);
    }

    //diyEntity 생성
    DiyEntity diyEntity = DiyEntity.builder()
            .packageName(packageFormDTO.getPackageName())
            .profileImg(packageFormDTO.getProfileImg())
            .shortDescription(packageFormDTO.getShortDescription())
            .regDate(LocalDate.now())
            .airline(airlineEntity)
            .route(routeEntity)
            .userNum(requestDTO.getUserNum()) // user부분 추후 수정
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

    DiyEntity diyEntity = diyRepository.findById(packageNum).orElse(null);

    //로그인한 유저가 응원하기 참여했는지 확인
    boolean isLiked = diyLikeRepository.existsByUserNumAndDiy(currentUserNum, diyEntity);

    //항공,루트, 상세 코스 조회
    AirlineEntity airlineEntity = diyEntity.getAirline();
    RouteEntity routeEntity = diyEntity.getRoute();
    List<DetailCourseEntity> detailCourseEntities = detailCourseRepository.findByRoute(routeEntity);

    // AirlineDTO 설정
    AirlineDTO airlineDTO = AirlineDTO.builder()
            .airlineNum(airlineEntity.getAirlineNum())
            .airlineName(airlineEntity.getAirlineName())
            .startingPoint(airlineEntity.getStartingPoint())
            .destination(airlineEntity.getDestination())
            .startFlightNum(airlineEntity.getStartFlightNum())
            .boardingDate(airlineEntity.getBoardingDate())
            .comeFlightNum(airlineEntity.getComeFlightNum())
            .comingDate(airlineEntity.getComingDate())
            .build();

    // RouteDTO 설정
    RouteDTO routeDTO = RouteDTO.builder()
            .routeNum((routeEntity.getRouteNum()))
            .startDate(routeEntity.getStartDate())
            .lastDate(routeEntity.getLastDate())
            .build();

    // DetailCourseDTO 설정
    List<DetailCourseDTO> detailCourseDTOList = detailCourseEntities.stream()
            .map(detailCourse -> DetailCourseDTO.builder()
                    //.detailCourseNum(detailCourse.getDetailCourseNum())
                    .dayNum(detailCourse.getDayNum())
                    //.routeNum(detailCourse.getRoute().getRouteNum())
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

    // PackageFormDTO설정
    PackageFormDTO packageFormDTO = PackageFormDTO.getDiyEntity(diyEntity);

    //임시 userNum
    Long userNum = diyEntity.getUserNum();

    // ResponseDTO 설정
    ResponseDTO responseDTO = ResponseDTO.builder()
            .airline(airlineDTO)
            .route(routeDTO)
            .packageForm(packageFormDTO)
            .detailCourses(detailCourseDTOList)
            .userNum(userNum)
            .likedNum(diyEntity.getPackageLikedNum())
            .viewNum(diyEntity.getPackageViewNum())
            .isLiked(isLiked)
            .build();
    return responseDTO;
  }

  @Transactional
  public DiyEntity updateDiy(Long packageNum, RequestDTO requestDTO) {
    // 1. 엔티티 조회
    DiyEntity diyEntity = diyRepository.findById(packageNum).orElse(null);
    if (diyEntity == null) {
      // 해당 번호의 DIY 패키지가 존재하지 않는 경우 예외 처리
      return null;
    }
    // 2. 관련 엔티티 조회
    AirlineEntity airlineEntity = airlineRepository.findById(diyEntity.getAirline().getAirlineNum())
            .orElseThrow(() -> new RuntimeException("항공사가 존재하지 않습니다."));

    RouteEntity routeEntity = routeRepository.findById(diyEntity.getRoute().getRouteNum())
            .orElseThrow(() -> new RuntimeException("경로가 존재하지 않습니다."));

    List<DetailCourseDTO> detailCourseDTOs = requestDTO.getDetailCourses();
    PackageFormDTO packageFormDTO = requestDTO.getPackageForm();
    //3. dto의 값을 엔티티에 복사
    BeanUtils.copyProperties(requestDTO.getAirline(),airlineEntity);
    BeanUtils.copyProperties(requestDTO.getRoute(),routeEntity);
    //4. 각 테이블에 저장
    airlineEntity = airlineRepository.save(airlineEntity);
    routeEntity = routeRepository.save(routeEntity);

    //5. 기존의 DetailCourseEntity를 삭제하고 새로 저장
    detailCourseRepository.deleteByRoute(routeEntity); //기존 경로와 연결된 detailcourseEntity
    for (DetailCourseDTO detailCourseDTO : detailCourseDTOs) {
      // RouteEntity를 DetailCourseDTO에 설정
      //detailCourseDTO.setRouteNum(routeEntity.getRouteNum());
      DetailCourseEntity detailCourseEntity = detailCourseDTO.toEntity(routeEntity);
      detailCourseRepository.save(detailCourseEntity); // 저장
    }

    // 6.diyEntity의 필드 업데이트
    BeanUtils.copyProperties(packageFormDTO,diyEntity);
    diyEntity.setModDate(LocalDate.now());
    diyEntity.setAirline(airlineEntity);
    diyEntity.setRoute(routeEntity);

    return diyRepository.save(diyEntity);
  }
@Transactional
public DiyEntity updateDiyPatch(Long packageNum, RequestDTO requestDTO) {
  // 1. 엔티티 조회
  DiyEntity diyEntity = diyRepository.findById(packageNum)
          .orElseThrow(() -> new RuntimeException("해당 번호의 DIY 패키지가 존재하지 않습니다."));

  // 2. 관련 엔티티 조회
  AirlineEntity airlineEntity = diyEntity.getAirline();
  RouteEntity routeEntity = diyEntity.getRoute();

  // 3. dto에 변경들어온 필드만 엔티티에 복사
  //airline
  if (requestDTO.getAirline() != null) {
    if (requestDTO.getAirline().getAirlineName() != null) {
      airlineEntity.setAirlineName(requestDTO.getAirline().getAirlineName());
    }
    if (requestDTO.getAirline().getStartFlightNum() != null) {
      airlineEntity.setStartFlightNum(requestDTO.getAirline().getStartFlightNum());
    }
    if (requestDTO.getAirline().getStartingPoint() != null) {
      airlineEntity.setStartingPoint(requestDTO.getAirline().getStartingPoint());
    }
    if (requestDTO.getAirline().getDestination() != null) {
      airlineEntity.setDestination(requestDTO.getAirline().getDestination());
    }
    if (requestDTO.getAirline().getBoardingDate() != null) {
      airlineEntity.setBoardingDate(requestDTO.getAirline().getBoardingDate());
    }
    if (requestDTO.getAirline().getComeFlightNum() != null) {
      airlineEntity.setComeFlightNum(requestDTO.getAirline().getComeFlightNum());
    }
    if (requestDTO.getAirline().getComingDate() != null) {
      airlineEntity.setComingDate(requestDTO.getAirline().getComingDate());
    }
    airlineEntity = airlineRepository.save(airlineEntity);
  }
  //route
  if (requestDTO.getRoute() != null) {
    if (requestDTO.getRoute().getStartDate() != null) {
      routeEntity.setStartDate(requestDTO.getRoute().getStartDate());
    }
    if (requestDTO.getRoute().getLastDate() != null) {
      routeEntity.setLastDate(requestDTO.getRoute().getLastDate());
    }
    routeEntity = routeRepository.save(routeEntity);
  }

  // 4. DetailCourse 업데이트
  List<DetailCourseDTO> detailCourseDTOs = requestDTO.getDetailCourses();
  if (detailCourseDTOs != null && !detailCourseDTOs.isEmpty()) {
    // 기존의 DetailCourse 삭제 후 새로 저장
    detailCourseRepository.deleteByRoute(routeEntity);
    for (DetailCourseDTO detailCourseDTO : detailCourseDTOs) {
      //detailCourseDTO.setRouteNum(routeEntity.getRouteNum());
      DetailCourseEntity detailCourseEntity = detailCourseDTO.toEntity(routeEntity);
      detailCourseRepository.save(detailCourseEntity);
    }
  }

  // 5. diyEntity의 필드 업데이트 (packageFormDTO로 업데이트)
  PackageFormDTO packageFormDTO = requestDTO.getPackageForm();
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
  }
  diyEntity.setModDate(LocalDate.now());
  diyEntity.setAirline(airlineEntity);
  diyEntity.setRoute(routeEntity);

  return diyRepository.save(diyEntity);
}

  @Transactional
  public void deleteDiy(Long packageNum){
    // 1. 엔티티 조회
    DiyEntity diyEntity = diyRepository.findById(packageNum).orElse(null);

    //2.diy패키지 테이블에서 삭제
    diyRepository.delete(diyEntity);

    //3. 관련 엔티티 조회
    AirlineEntity airlineEntity = airlineRepository.findById(diyEntity.getAirline().getAirlineNum())
            .orElseThrow(() -> new RuntimeException("항공사가 존재하지 않습니다."));

    RouteEntity routeEntity = routeRepository.findById(diyEntity.getRoute().getRouteNum())
            .orElseThrow(() -> new RuntimeException("경로가 존재하지 않습니다."));
    //연관된 테이블에서 삭제
    airlineRepository.delete(airlineEntity);
    routeRepository.delete(routeEntity);
    detailCourseRepository.deleteByRoute(routeEntity);
  }
}