## **레고레고 Spring boot Project**

---

스프링 부트 + 리액트 DIY 패키지 제작 사이트

### **🗺️ 프로젝트 소개**

---

기존의 정형화된 패키지 여행이 지겨운 사람들을 위한 새로운 여행 플랫폼

\- 정형화된 패키지 여행의 반복적인 일정에 지친 여행자들을 타켓으로, 자신만의 독특한 패키지 여행을 설계 후 번거로운 과정 없이 플랫폼을 통해 개성있는 여행을 실현할 수 있게 하기 위해 제작했습니다.

### **🗓️ 개발 기간**

---

-   24.06.12 ~ 24.07.17

  

#### **👥 팀원 구성**

팀장 : 김주희 - 상품, 주문, 결제, 여행 후기(CRUD), 실시간 알림, DIY 리스트 로직 구현, 공용 DB, PPT 제작, 발표

부팀장 : 정세은 - DIY 패키지 제작(CRUD), 추천 상품, DIY 리스트 로직 구현, 관리자/파트너 페이지, 커뮤니티 목록 페이지

팀원 : 김현진 - 마이페이지, 어바웃페이지,  홈페이지, 지난여행 페이지

팀원 : 이효정 - 상품 페이지, 상품 상세 페이지, 지난 여행 페이지, 지난여행 상세 페이지, 패키지 제작 지도 구현

팀원 : 조예린 - 로그인, 회원가입, ID 찾기, PW 찾기, 커뮤니티(CRUD), 프로필 수정 로직 구현


#### **⚙️ 개발 환경**

**BackEnd**

-   언어 : Java 17
-   IDE : IntelliJ
-   Framework : SpringBoot(3.3.0)
-   Database : MariaDB
-   ORM : JPA

**FrontEnd**

-   HTML, CSS, JavaScript
-   Framework : React
-   Disign : Figma


#### **📸 아키텍쳐**
- Home Page

<p align="center">
  <img src="https://github.com/user-attachments/assets/a7077c5a-2644-4e7a-9e10-384e3f31445b">
</p>

- DIY Page
  
<p align="center">
  <img src="https://github.com/user-attachments/assets/a3fa50b1-eb9b-4915-8ae6-c645f60d42cd">
</p>

- Product Page
  
<p align="center">
  <img src="https://github.com/user-attachments/assets/0b9b1650-2301-4849-8e67-13da11d931f5">
</p>

- Community Page
  
<p align="center">
  <img src="https://github.com/user-attachments/assets/22c60714-79e1-4280-b550-a6d8952b7c9c">
</p>



### **🧰 주요 기능**

---

**1\. 로그인**

 - ID 찾기

 - PW 찾기

**2\. 회원가입**

 - 이메일 중복 체크

 - 이메일 인증

**3\. DIY 패키지 제작**

\- 여행지, 날짜, 항공권 선택 : 한국공항공사 API 연동

\- 여행 일정 제작 : 구글 지도 API 연동

\- 임시저장

**4\. 상품 주문**

 - 결제 : 결제 API 연동

 - 환불

 - 자동 환불

**5\. 패키지 상품화**

 - 여행사 : 가격제안 (가격, 인원 수, 해택)

 - 작성자 : 제안수락

 - 관리자 : 상품 등록 (마감기한)

**6\. 여행 후기**

\- 여행 기간 지난 상품 이동

\- 리뷰 작성/수정/삭제

**7\. 커뮤니티 게시판**

 - 게시글 작성/수정/삭제

 - 댓글/대댓글 작성/수정/삭제

**8\. 마이페이지**

\- 프로필 변경

\- 회원 탈퇴

\- 패키지 조회 리스트

**9\. 실시간 알림**

\- 패키지 상품화 과정 알림



### **✅ 설치**

---

\- redis 설치 (Mac 버전)

```
$ brew install redis

$ brew services start redis

$ brew services stop redis

$ brew services restart redis
```
