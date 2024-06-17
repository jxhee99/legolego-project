package com.kosta.legolego.diypackage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name="DIY_like")
public class DiyLikeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "like_num")
  private Long likeNum;

  //추후 조인
  @Column(name = "user_num", nullable = false)
  private Long userNum;

  @ManyToOne
  @JoinColumn(name = "package_num", nullable = false)
  private DiyPackage diy;
}
