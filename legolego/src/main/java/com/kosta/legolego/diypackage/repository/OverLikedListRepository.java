package com.kosta.legolego.diypackage.repository;

import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OverLikedListRepository extends JpaRepository<OverLikedList, Long> {
  void deleteByDiyPackage(DiyPackage diyPackage);

}
