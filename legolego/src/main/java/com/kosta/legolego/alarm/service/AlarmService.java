package com.kosta.legolego.alarm.service;

import com.kosta.legolego.alarm.entity.Alarm;
import com.kosta.legolego.alarm.repository.AlarmRepository;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AlarmService {

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${admin.id}")
    private Long adminNum;

    // user용 알림
    public void sendAlarmToUser(Long userNum, String message) {
        sendAlarm(userNum, "USER", message);
    }
    // partner용 알림
    public void sendAlarmToAdmin(String message) {
        sendAlarm(adminNum, "ADMIN", message);
    }
    // admin용 알림
    public void sendAlarmToPartner(Long partnerNum, String message) {
        sendAlarm(partnerNum, "PARTNER", message);
    }

    // 알림 내용 저장 후 전송
    public void sendAlarm(Long id, String role, String message) {
        Alarm alarm = new Alarm();
        alarm.setMemberId(id);
        alarm.setRole(role);
        alarm.setMessage(message);
        alarm.setDate(new Timestamp(System.currentTimeMillis()));

        alarmRepository.save(alarm);

        // websocket 메시지 전송
        messagingTemplate.convertAndSend("/topic/alarm/" + id, message);
        log.info("Message sent to /topic/alarm/" + id + ": " + message);
    }

    public List<Alarm> getAlarmForMember(Long memberId, String role) {
        return alarmRepository.findByMemberIdAndRole(memberId, role);

    }

    // 클라이언트 알림 확인 상태 업데이트
    public boolean checkAlarm(Long alarmNum, Long memberId){
        Optional<Alarm> alarmOpt = alarmRepository.findById(alarmNum);

        if(alarmOpt.isPresent()) {
            Alarm alarm = alarmOpt.get();

            if(alarm.getMemberId().equals(memberId)) {
                alarm.setIsChecked(true); // 알림 확인하면

                alarmRepository.save(alarm);
                alarmRepository.delete(alarm); // 해당 알림 테이블에서도 삭제
                return true;
            }
        }
        return false;
    }

    @Async
    public void sendAlarmList(DiyList savedDiyList, DiyPackage diyPackage) {
        log.info("sendAlarmList called with DiyList: {}", savedDiyList);
        // 알림 전송
        try {
            Long userNum = diyPackage.getUser().getUserNum();
            log.info("user 정보 : {}", userNum);
            String message = savedDiyList.getPartner().getCompanyName() + "에서 제안 요청을 보냈습니다.";
            log.info("partner 정보 : {}", message);

            Alarm alarm = new Alarm();
            alarm.setMemberId(userNum);
            alarm.setRole("USER");
            alarm.setMessage(message);
            alarm.setDate(new Timestamp(System.currentTimeMillis()));

            alarmRepository.save(alarm);
            log.info("Alarm saved: {}", alarm);
            messagingTemplate.convertAndSend("/topic/alarm/" + userNum, message);
            log.info("Websocket message sent to /topic/alarm/{}", userNum);
        } catch (Exception e) {
            log.error("Error in sendAlarmList: ", e);
        }
    }

}
