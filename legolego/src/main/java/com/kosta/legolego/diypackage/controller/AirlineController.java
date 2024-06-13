package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.service.AirlineService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirlineController {
  @Value("${service.api.key}")
  private String apiKey;

  @Autowired
  AirlineService service;
  @GetMapping("/api/airline")
  public String getFlightSchedule(@RequestParam String schDate,
                                  @RequestParam String returnDate,
                                  @RequestParam String schDeptCityCode,
                                  @RequestParam String schArrvCityCode) {
    // 가는 날 스케줄 데이터 가져오기
    String scheduleData = service.fetchFlightData(schDate, schDeptCityCode, schArrvCityCode);
    JSONArray startDataArray;
    if (scheduleData == null) {
      startDataArray = new JSONArray();
    } else {
      startDataArray = service.parseAndExtractFlightData(scheduleData);
    }

    // 오는 날 스케줄 데이터 가져오기
    String returnScheduleData = service.fetchFlightData(returnDate, schArrvCityCode, schDeptCityCode);
    JSONArray returnDataArray;
    if (returnScheduleData == null) {
      returnDataArray = new JSONArray();
    } else {
      returnDataArray = service.parseAndExtractFlightData(returnScheduleData);
    }


    // 두 array 합쳐서 하나의 JSON 객체로 반환
    JSONObject result = new JSONObject();
    result.put("startData", startDataArray);
    result.put("returnData", returnDataArray);

    return result.toString(4); // 정렬된 JSON 문자열 반환
  }

}
