package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.service.AirlineService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
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
  public String getFlightSchedule(@RequestParam("schDate") String schDate,
                                  @RequestParam("returnDate") String returnDate,
                                  @RequestParam("schDeptCityCode") String schDeptCityCode,
                                  @RequestParam("schArrvCityCode") String schArrvCityCode) {
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

  @GetMapping("/api/airport-code")
  public String getAirportCode(@RequestParam("cityKor") String cityKor) {

    //공항코드 가져오기
    String airportCode = service.fetchAirportCode();

    JSONObject jsonObject = XML.toJSONObject(airportCode);
    JSONObject response = jsonObject.getJSONObject("response");
    JSONObject body = response.getJSONObject("body");
    JSONObject items = body.getJSONObject("items");
    JSONArray itemArray = items.getJSONArray("item");

    // itemArray 배열을 순회하여 cityKor 값이 일치하는 객체 찾기
    for (int i = 0; i < itemArray.length(); i++) {
      JSONObject item = itemArray.getJSONObject(i);
      if (item.getString("cityKor").equals(cityKor)) {
        String cityCode = item.getString("cityCode");
        JSONObject result = new JSONObject();
        result.put("cityCode", cityCode);
        return result.toString();
      }
    }

    // 일치하는 cityKor가 없을 경우 null 반환
    JSONObject result = new JSONObject();
    result.put("cityCode", JSONObject.NULL);
    return result.toString();
  }

}
