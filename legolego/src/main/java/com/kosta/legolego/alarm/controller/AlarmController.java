package com.kosta.legolego.alarm.controller;

import com.kosta.legolego.alarm.entity.Alarm;
import com.kosta.legolego.alarm.service.AlarmService;
import com.kosta.legolego.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @GetMapping("/user/alarms")
    public ResponseEntity<List<Alarm>> getAlarmsForUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long memberId = userDetails.getId();
        List<Alarm> alarms = alarmService.getAlarmForMember(memberId, "USER");
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/partner/alarms")
    public ResponseEntity<List<Alarm>> getAlarmsForPartner(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails == null || !userDetails.getRole().equals("ROLE_PARTNER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long memberId = userDetails.getId();
        List<Alarm> alarms = alarmService.getAlarmForMember(memberId, "PARTNER");
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/admin/alarms")
    public ResponseEntity<List<Alarm>> getAlarmsForAdmin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails == null || !userDetails.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long memberId = userDetails.getId();
        List<Alarm> alarms = alarmService.getAlarmForMember(memberId, "ADMIN");
        return ResponseEntity.ok(alarms);
    }

    // 클라이언트 알림 확인
    @PutMapping("/alarms/{alarm_num}/check")
    public ResponseEntity<Void> checkAlarm(@PathVariable("alarm_num") Long alarmNum, @AuthenticationPrincipal CustomUserDetails userDetails ) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isUpdated = alarmService.checkAlarm(alarmNum, userDetails.getId());
        if(isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

}
