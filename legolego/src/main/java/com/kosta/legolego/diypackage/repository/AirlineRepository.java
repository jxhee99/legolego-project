package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.AirlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<AirlineEntity, Long> {
}
