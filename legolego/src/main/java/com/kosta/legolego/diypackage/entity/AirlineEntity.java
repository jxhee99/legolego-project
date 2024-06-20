package com.kosta.legolego.diypackage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="airline")
public class AirlineEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "airline_num")
  private Long airlineNum;

  @Column(name = "start_airline_name", nullable = false, length = 50)
  private String startAirlineName;

  @Column(name = "starting_point", nullable = false, length = 50)
  private String startingPoint;

  @Column(name = "destination", nullable = false, length = 50)
  private String destination;

  @Column(name = "start_flight_num", nullable = false, length = 20)
  private String startFlightNum;

  @Column(name = "boarding_date", nullable = false)
  private LocalDateTime boardingDate;

  @Column(name = "come_airline_name", nullable = false, length = 50)
  private String comeAirlineName;

  @Column(name = "come_flight_num", nullable = false, length = 20)
  private String comeFlightNum;

  @Column(name = "coming_date", nullable = false)
  private LocalDateTime comingDate;
}
