package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AirlineRepository extends JpaRepository<AirlineEntity, Long> {
  //목적지별 조회에 필요한 쿼리
  List<AirlineEntity> findByDestinationContainingOrderByAirlineNumDesc(String destination);

  //월별 조회에 필요한 쿼리
  @Query("SELECT a FROM AirlineEntity a WHERE FUNCTION('MONTH', a.boardingDate) = :month ORDER BY a.airlineNum DESC")
  List<AirlineEntity> findByMonth(@Param("month") int month);

  // 목적지와 월별 조회에 필요한 쿼리
  @Query("SELECT a FROM AirlineEntity a WHERE a.destination LIKE %:destination% AND FUNCTION('MONTH', a.boardingDate) = :month ORDER BY a.airlineNum DESC")
  List<AirlineEntity> findByDestinationAndMonth(@Param("destination") String destination, @Param("month") int month);

}
