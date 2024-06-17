package com.kosta.legolego.diypackage.entity;

import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name="DIY_package")
public class DiyPackage {
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

  @Builder.Default
  @Column(name = "package_liked_num", nullable = false)
  private int packageLikedNum = 0;

  @Builder.Default
  @Column(name = "package_view_num", nullable = false)
  private int packageViewNum = 0;

  @Column(name = "short_description", nullable = false)
  private String shortDescription;
  
  @Column(name = "package_approval", nullable = false)
  private Boolean packageApproval = false;

  @OneToMany(mappedBy = "diyPackage", cascade = CascadeType.ALL)
  private List<DiyList> diyLists = new ArrayList<>();
  
  //추후 조인
  @ManyToOne
  @JoinColumn(name = "user_num", nullable = false)
  private User user;

  @OneToOne
  @JoinColumn(name = "airline_num", nullable = false)
  private AirlineEntity airline;

  @OneToOne
  @JoinColumn(name = "route_num", nullable = false)
  private RouteEntity route;

}
