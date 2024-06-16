package com.kosta.legolego.diypackage.service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Service
public class AirlineService {
  @Value("${service.api.key}")
  private String apiKey;

  //항공스케줄 정보 조회
  public String fetchFlightData(String schDate, String schDeptCityCode, String schArrvCityCode) {
    StringBuffer result = new StringBuffer();
    try {
      String apiUrl = String.format(
              "http://openapi.airport.co.kr/service/rest/FlightScheduleList/getIflightScheduleList?ServiceKey=%s&schDate=%s&schDeptCityCode=%s&schArrvCityCode=%s&pageNo=1&numOfRows=20",
              apiKey, schDate, schDeptCityCode, schArrvCityCode
      );
      URL url = new URL(apiUrl);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.connect();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
      String returnLine;
      while ((returnLine = bufferedReader.readLine()) != null) {
        result.append(returnLine);
      }
      bufferedReader.close();

      // 스케줄이 없을 때 처리
      JSONObject jsonObject = XML.toJSONObject(result.toString());
      JSONObject response = jsonObject.getJSONObject("response");
      JSONObject body = response.getJSONObject("body");
      int totalCount = body.getInt("totalCount");
      if (totalCount == 0) {
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result.toString();
  }

  //필요한 정보만 반환
  public JSONArray parseAndExtractFlightData(String xmlData) {
    JSONArray extractedDataArray = new JSONArray();
    Set<String> uniqueFlights = new HashSet<>();

    try {
      JSONObject jsonObject = XML.toJSONObject(xmlData);
      JSONObject response = jsonObject.getJSONObject("response");
      JSONObject body = response.getJSONObject("body");
      JSONObject items = body.getJSONObject("items");
      JSONArray itemArray = items.getJSONArray("item");

      // 필요한 데이터 추출
      for (int i = 0; i < itemArray.length(); i++) {
        JSONObject item = itemArray.getJSONObject(i);
        String airlineKorean = item.getString("airlineKorean");
        String airport = item.getString("airport");
        String city = item.getString("city");
        String internationalNum = item.getString("internationalNum");
        String internationalTime = item.get("internationalTime").toString();

        // 중복 확인
        String uniqueKey = internationalNum + "-" + internationalTime;
        if (!uniqueFlights.contains(uniqueKey)) {
          uniqueFlights.add(uniqueKey);

          // 새로운 JSON 객체에 데이터 추가
          JSONObject extractedData = new JSONObject();
          extractedData.put("airlineKorean", airlineKorean);
          extractedData.put("airport", airport);
          extractedData.put("city", city);
          extractedData.put("internationalNum", internationalNum);
          extractedData.put("internationalTime", internationalTime);
          extractedDataArray.put(extractedData);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return extractedDataArray;
  }

  //공항코드 조회
  public String fetchAirportCode() {
    StringBuffer result = new StringBuffer();
    try {
      String apiUrl = String.format(
              "http://openapi.airport.co.kr/service/rest/AirportCodeList/getAirportCodeList?ServiceKey=%s&numOfRows=1352",
              apiKey
      );
      URL url = new URL(apiUrl);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.connect();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
      String returnLine;
      while ((returnLine = bufferedReader.readLine()) != null) {
        result.append(returnLine);
      }
      bufferedReader.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return result.toString();
  }
}
