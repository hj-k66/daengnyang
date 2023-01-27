# [2주차]idea_2팀_진행상황_공유  

## 팀 구성원, 개인 별 역할

---

### 조예지 `PM`

- 그룹 관련 api 개발

### 김희정 `CTO`

- Access Token, Refresh Token 이용한 login 구현
- Redis 이용해 Refresh Token 저장

### 조문주 `기획`

- 로그인, 회원가입, 캘린더 ui 구현

### 백승근 `인프라`

- 일기 관련 api 개발

### 이상훈 `기획`

- 반려동물 관련 api 개발

### 김민경 `인프라`

- 일정 관련 api 개발

## 팀 내부 회의 진행 회차 및 일자

---

- 6회차(2023.01.25) 슬랙 허들 (불참인원 없음)
    - 야간멘토님 점검 일자
    - 테스트코드 리팩토링
- 7회차(2023.01.26) 슬랙 허들 (불참 : 승근님)
    - 코드 리뷰
- 8회차(2023.01.27) 슬랙 허들
    - 코드 리뷰

## 현재까지 개발 과정 요약

---

### 조예지

- 이번주엔 실제 기능 개발을 시작하였다. 하다보니 api 정의나 엔티티에서도 수정할 부분들이 계속 생겼다.
  엔티티의 컬럼 타입도 변경하게 되었고, api를 추가해야할 부분도 있었다.
- 생일을 설계 시에는 LocalDateTime 타입으로 받았는데, 나이를 계산하다보니 시간이 방해가 되었다. 그래서 LocalDate로 변경하였다.
- 테스트 코드에서  REST 문서 작성을 위해 추가되는 어노테이션이나 코드들이 많았다. 이러한 중복을 클래스로 빼내어 상속할 수 있게 리팩토링하였다.

### 이상훈

- 반려동물 생일을 받아서 나이로 변환하는 작업을 고민하고있었는데, 예지님이 생일 데이터 타입을LocalDateTime → LocalDate 변경하고 나이 구하는 메서드를 만들어주셔서 해결됐다.
- Test작성
    - given 조건에서 any() 사용을 지양하려 노력중이다.
    - Rest Docs 적용중
    - Page를 파라미터로 받는데 어려움을 겪고 있었는데 해결했다.
      `Page<PetShowResponse> pages = new PageImpl<>(Arrays.*asList*(petShowResponse));`
    - 리스트 조회 성공 테스트에 파라미터 검증하는 부분에서 에러가 계속나서 보류해 두었다.
    - 어떤 테스트를 만들어야하나 고민을 하게된다.

### 백승근

- 일기 관련 CRUD api 개발을 이번 주 부터 시작했다.
- 개인 프로젝트 때 했던 게시판 CRUD와 요구하는 기능이나 엔티티 자체도 많이 달라서
- 또 다른 느낌과 어려움을 느꼈다.
- 테스트 코드에 공을 들이고 있는데 가장 고민이 많이 가는 부분같다.

### 김희정

- 로그인 구현 시 Refresh Token을 도입하기 위해 관련 자료조사를 진행했다.
- Refresh Token 저장을 Redis에서 진행하기로 결정했다.
- 로컬에 Redis를 설치하고 코드를 짜고 있는데 Redis Repository방식으로 짤지 Redis Template 방식으로 할지 고민이다.
- Refresh token에 어떤 정보를 담을지 어떻게 구현할지, RTR은 어떻게 구현할지 자료마다 제각각이라 헷갈린다.

### 김민경

- 개발을 시작하고 몇일간은 인프라 작업을한다고 기능개발을 못하고 있다가 뒤늦게 시작하게 되었다. 개발의 전체적인 부분을 파악하는데 시간이 좀 걸렸고, 맡게 된 일정 부분도 엔티티 컬럼이 많다보니 복잡하고 헷갈리는 부분이 조금 있었다.
- 팀원분들이 모르는 부분은 붙잡고 같이 해결해주셔서 혼자하기 어려웠던 부분들을 조금 더 빨리, 쉽게 해결 할 수 있었다.
- 기능 중 중요한 부분인 일정 등록, 수정 부터 작업을 했는데 Request, Response에 필요,불필요한 컬럼들을 한번더 체크해서 상의 후 추가하거나 삭제했다.

### 조문주

- 지난주에 작성한 UI 정의서와 프로토타입을 참고해서 페이지들을 구현하였다. 꼼꼼하게 작성하려고 노력했지만 구현하는 과정에서 계속 추가하거나 수정할 부분이 생겨서 그때그때 팀원들과 상의하며 수정했다.
- 타임리프를 활용해서 공통 레이아웃인 헤더를 가장 먼저 구현했는데 타임리프 문법이 익숙하지 않아서 경로를 지정하는 부분이 많이 헷갈렸다.
- 캘린더 페이지에 fullcalendar 라이브러리를 활용했고 반려동물 등록 페이지에는 jQuery 달력 위젯인 datepicker를 활용했는데 아직 이해도가 높지 않아서 원하는 만큼의 기능 구현을 하지 못한점이 아쉽다.
- 이슈를 확인하고 브랜치를 생성해서 작업하면서 깃 사용에 대해 조금 더 알게 된 것 같다.

## 개발 과정에서 나왔던 질문

---

### 1. RestDoc 작성 중 PathVariable 작성 법

- pathParameters*를 추가하면 아래와 같은 에러가 나온다.

```java
urlTemplate not found. If you are using MockMvc did you use RestDocumentationRequestBuilders to build the request?
```

path variable을 표시하기 위해서는 MockMvcBuilders보다 RestDocumentationRequestBuilders를 이용하는 것이 좋다고 한다.

따라서 static 메서드 앞에 클래스를 명시해주고 url template 도 수정해준다.

```java
mockMvc.perform(
      **RestDocumentationRequestBuilders.get("/api/v1/groups/{groupId}/users", 1L)**
              .contentType(MediaType.APPLICATION_JSON))
.andExpect(status().isOk())
.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
.andExpect(jsonPath("$.result.users").exists())
.andDo(
      restDocs.document(
              **pathParameters(
                      parameterWithName("groupId").description("그룹 번호")
              ),**
              responseFields(
                      fieldWithPath("resultCode").description("결과코드"),
                      fieldWithPath("result.users").description("그룹유저 리스트"),
											...
```

### 2. 일정 등록, 수정 테스트코드 작성 중 LocalDateTime 에러

- LocalDateTime을 포함한 객체를 ObjectMapper 함수를 사용해 가져올 때 아래와 같은 에러 발생

```bash
Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest["dueDate"])
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.daengnyangffojjak.dailydaengnyang.domain.dto.schedule.ScheduleCreateRequest["dueDate"])
	at app//com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:77)
```

검색해보니 Java 8 이후에 추가된  LocalDateTime을 직렬화 또는 역직렬화를 못하는 현상이 발생되어 해당 에러가 난다고 한다.

해결방법으로는 아래의 `jackson-datatype-jsr310` 라이브러리를 의존성 추가하고,

```bash
implementation group: 'com.fasterxml.jackson.datatype', name: 'jackson-datatype-jsr310', version: '2.14.1'
```

objectMapper 함수 사용 시 `.registerModule(new JavaTimeModule())` 을 추가로 사용하여 해결한다.

```bash
.content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsBytes([LocalDateTime을 포함한 객체]))
```

## 개발 결과물 공유

---

📎 [Repository](https://gitlab.com/daengnyangffouchak/daily-daengnyang)

🖥 [블로그](https://daengnyangproject.tistory.com/)