package com.kosta.legolego.alarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_num")
    private Long alarmNum;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "role")
    private String role;

    @Column(name = "message")
    private String message;

    @Column(name = "date")
    private Timestamp date;

    @Column(name = "is_checked")
    private Boolean isChecked; // 클라이언트 확인용

}
