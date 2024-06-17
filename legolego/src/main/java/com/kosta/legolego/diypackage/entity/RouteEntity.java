package com.kosta.legolego.diypackage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="route")
public class RouteEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "route_num")
  private Long routeNum;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "last_date", nullable = false)
  private LocalDate lastDate;
}
