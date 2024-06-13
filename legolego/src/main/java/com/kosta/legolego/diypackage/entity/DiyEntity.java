package com.kosta.legolego.diypackage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name="DIY_package")
public class DiyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "package_num")
  private Long packageNum;

  @Column(name = "package_name", nullable = false, length = 255)
  private String packageName;

  @Column(name = "profile_img", nullable = false, length = 255)
  private String profileImg;

  @Column(name = "reg_date", nullable = false)
  private LocalDate regDate;

  @Column(name = "mod_date")
  private LocalDate modDate;

  @Column(name = "package_liked_num", nullable = false)
  private int packageLikedNum = 0;

  @Column(name = "package_view_num", nullable = false)
  private int packageViewNum = 0;

  @Column(name = "short_description", nullable = false)
  private String shortDescription;

  //추후 조인
  @Column(name = "user_num", nullable = false)
  private Long userNum;

  @OneToOne
  @JoinColumn(name = "airline_num", nullable = false)
  private AirlineEntity airline;

  @OneToOne
  @JoinColumn(name = "route_num", nullable = false)
  private RouteEntity route;

}
