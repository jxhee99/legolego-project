package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiyRepository extends JpaRepository<DiyEntity, Long> {
}
