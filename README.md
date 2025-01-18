

# Stock Discussion

## 📕 프로젝트 소개
**Stock Discussion**은 주식 시장의 다양한 정보와 의견을 실시간으로 공유할 수 있는 플랫폼입니다.

MSA 환경에서 설계되어 SNS, 주식 데이터, 실시간 처리 서비스를 각각 독립적으로 운영합니다. 각 서비스는 기능에 맞는 최적화된 데이터베이스를 사용하며, Kafka와 WebSocket을 통해 실시간 데이터 처리를 구현하여 사용자에게 빠르고 정확한 정보를 제공합니다.

---
### 프로젝트 목표

1. **대규모 데이터 처리**  
   WebSocket과 SSE 기반의 비동기 처리로 높은 동시 사용자 트래픽을 안정적으로 처리하고, Kafka를 활용해 이벤트 기반 데이터 흐름을 효율적으로 관리.

2. **안정성과 성능 최적화**  
   Spring WebFlux를 활용해 논블로킹 I/O로 실시간 데이터의 전송 속도를 최적화하고, Redis 캐싱을 통해 데이터 접근 속도를 향상.

3. **MSA 기반 확장성**  
   Spring Cloud Gateway와 Eureka를 활용한 MSA 환경으로 서비스 간 독립성을 확보하고, 오토스케일링을 통해 높은 트래픽에도 유연하게 대응.


---

<details>
  <summary><span style="font-size: 21px;">📷 <b>View</b></span></summary>

  <table style="border-collapse: collapse; border: none;">
    <tr style="border: none;">
      <td style="border: none;">
        <img src="https://github.com/user-attachments/assets/bb9917ff-8229-44b9-b37e-e2c3b52f792d" width="400"/>
      </td>
      <td style="border: none;">
        <img src="https://github.com/user-attachments/assets/d7b33490-0d20-4e8f-8a10-18f5843579fa" width="400" />
      </td>
    </tr>
    <tr style="border: none;">
      <td style="border: none;">
        <img src="https://github.com/user-attachments/assets/c61dbaa0-0409-463e-b120-88e5e9087fac" width="400"/>
      </td>
      <td style="border: none;">
        <img src="https://github.com/user-attachments/assets/9ea67c90-23b3-4146-9b40-9e5017f68ad9" width="400"/>
      </td>
    </tr>
  </table>

</details>

<details>
  <summary><span style="font-size: 21px;">📂 System Architecture</span></summary>
  <img src="https://github.com/user-attachments/assets/3bff9c68-7667-4606-9f19-f7ebdca59a20" alt="Architecture" style="border: none;">
</details>

<details>
  <summary><span style="font-size: 21px;">📊 ERD</span></summary>
  <img src="https://github.com/user-attachments/assets/4091b794-ef9b-4a23-8ac9-4d10908863e3" alt="ERD" style="border: none;" >
</details>

<details>
  <summary><span style="font-size: 21px;">📋 API 문서</span></summary>

- [View API Documentation](docs/api-documentation.html)
</details>

<details>
  <summary><span style="font-size: 21px;">🛠️ 기술 스택</span></summary>

### **Backend**
Spring Boot, Spring Cloud Gateway, Eureka, OpenFeign, Spring Security, Spring WebFlux, Apache Kafka

### **Database**
MySQL, PostgreSQL, Mongo, Redis (캐싱)

### **Real-Time Communication**
WebSocket, Server-Sent Events (SSE)

### **External API Integration**
Upbit Open API

### **Monitoring & Testing**
Grafana, InfluxDB, K6, Telegraf

### **Infrastructure**
Docker, Docker Compose

</details>

---





## 주요 서비스 기능

### 1. **User Service**
- 회원 관리: 회원가입, 이메일 인증, JWT 토큰 발급, 프로필 수정.
- 팔로우 관리: 사용자 팔로우/언팔로우, 팔로워 및 팔로잉 목록 조회.
- 파일 업로드: 프로필 이미지 업로드 및 저장.

### 2. **Post Service**
- 게시글 관리: 게시글 작성, 수정, 삭제 및 조회.
- 게시글 좋아요: 좋아요 및 좋아요 취소 기능.
- 게시글 상태 관리: 조회수 증가, 댓글 수 업데이트, 좋아요 수 관리.

### 3. **Comment Service**
- 댓글 관리: 댓글 및 대댓글 작성, 수정, 삭제, 조회.
- 댓글 좋아요: 좋아요 및 좋아요 취소 기능.

### 4. **Stock Service**
- 실시간 데이터 처리: Upbit Ticker 및 Minute 데이터 수신.
- 캔들 데이터 관리: 일봉, 주봉, 월봉, 연봉 데이터 조회 및 저장.
- 배치 작업: 캔들 데이터를 주기적으로 수집 및 저장.

### 5. **LiveChat Service**
- 종목별 실시간 채팅방 제공
- WebSocket 연결 관리: 사용자 검증 및 세션 관리.
- 채팅방 관리: Redis 기반 채팅방 캐싱, 조회 및 삭제.
- 메시지 배치 처리: 메시지 배치 저장 및 재시도 처리.

### 6. **Activity Service**
- 알람 관리: 활동 기반 알람 생성, 실시간 SSE 알람 전송.
- 유저 활동 로그 관리: 이벤트 처리 및 로그 저장.

---

## 핵심 기능

- **실시간 채팅 (Reactive)**: WebSocket과 Spring WebFlux 기반 비동기 논블로킹 실시간 채팅.
- **Server-Sent Events (SSE, Reactive)**: 실시간 알림을 안정적으로 제공.
- **업비트 실시간 정보 제공**: Upbit API를 통해 실시간 Ticker 및 캔들 데이터 수집.

---

## 기능 플로우

### 유저 활동 기록을 기반으로 한 알림 프로세스 요약 및 기능 플로우

<table style="border-collapse: collapse; border: none;">
  <tr style="border: none;">
    <td style="border: none;">
      <img src="https://github.com/user-attachments/assets/9c414731-4b27-44e8-aa22-78ed6b9f998e" alt="활동 기록 생성 이미지" style="width: 400px; height: 400px; object-fit: contain;" />
    </td>
    <td style="border: none;">
      <img src="https://github.com/user-attachments/assets/566be510-d881-4799-aba6-3a6dad155e09" alt="알림 전송 이미지" style="width: 415px; height: 400px; object-fit: contain;" />
    </td>
  </tr>
</table>

- **활동 기록 생성**: 사용자가 게시글을 작성하거나 팔로우 요청 등을 수행하면 서버가 이벤트를 발행하고, `ActivityService`가 이를 구독하여 유저 활동을 기록.
- **실시간 알림 전송**: 기록된 활동 정보를 기반으로 알림을 생성하고, **SSE(Server-Sent Events)**를 통해 실시간으로 사용자에게 전송.
- **알림 저장**: 생성된 알림은 데이터베이스에 저장되어 관리.

---

### 실시간 채팅 기능 플로우

<div style="text-align: center;">
  <img src="https://github.com/user-attachments/assets/f204db43-9b3b-4c6d-9290-c7cca518159f" alt="실시간 채팅 흐름 이미지" style="width: 600px; height: 600px; object-fit: contain; display: block; margin: auto;" />
</div>

- **WebSocket 연결 및 정보 확인**: 사용자 연결 요청 시, Redis에서 사용자와 채팅방 정보를 확인하고, 필요 시 Database에서 조회.
- **메시지 처리 및 브로드캐스트**: LiveChatService가 메시지를 브로드캐스트하고, MessageQueue를 통해 비동기 저장.
- **효율적 데이터 관리**: Redis 캐싱으로 데이터 조회 속도 향상 및 비동기 처리를 통한 시스템 성능 최적화.
---
## 웹소켓 채팅 성능 최적화 사례


### 1차 개선 [version 0.1 보기](https://github.com/wer158177/Stock-discussion/wiki/%EC%84%B1%EB%8A%A5%ED%85%8C%EC%8A%A4%ED%8A%B8-1%EC%B0%A8%EA%B2%B0%EA%B3%BC)

- **목표**: 데이터베이스 부하 감소 및 WebSocket 메시지 처리 최적화.

1. **JPA 제거 및 JDBC 사용**:
    - JPA 영속성 컨텍스트 관리로 인해 불필요한 조회/삽입 쿼리가 발생.
    - JDBC 배치 처리로 대체하여 필요한 쿼리만 실행.
    - 캐싱된 채팅방 정보를 사용해 메시지 저장  연관 관계 제거.
2. **배치 인서트 도입**:
    - 메시지 큐에 담긴 메시지를 일정량 모아 한 번에 삽입.
    - 데이터베이스 연결 횟수 감소.
3. **캐싱 활용**:
    - 채팅방 이름과 관련 데이터를 캐싱하여 데이터베이스 조회 횟수를 최소화.

    
- **주요 성과**:
    - **수신 메시지 수**: **311,236 → 1,124,936** (**261.40% 증가**).
    - **수신 데이터량**: **66 MB → 224 MB** (**239.39% 증가**).
    - **최대 응답 시간**: **140,023 ms → 137,516 ms** (**1.79% 단축**).
    - 메시지 처리 안정성과 효율성 향상.



### 2차 개선 [version 0.2 보기](https://github.com/wer158177/Stock-discussion/wiki/%EC%84%B1%EB%8A%A5%ED%85%8C%EC%8A%A4%ED%8A%B8-2%EC%B0%A8%EA%B2%B0%EA%B3%BC)

- **목표**: Spring WebFlux 기반 리액티브 전환으로 동시성 및 비동기 처리 강화.


- **주요 성과**:
    - **WebSocket 연결 시간**: **5.70ms → 1.74ms** (**-69.5% 단축**).
    - **수신 메시지 속도**: **94,235 msg/s → 97,973 msg/s** (**+4.0% 증가**).
    - **응답 시간**: **57,463 ms → 50,485 ms** (**-12.1% 단축**).
    - **수신 데이터량**: **2.7 GB → 3.2 GB** (**+18.5% 증가**).

### 3차 개선 
    로컬에서는 의미가없었음 메모리나 사용량에 한계가있어서 임계점이상으로는 처리가 잘되지않음
    브로드 캐스트는 정상적으로 작동하나 데이터베이스에 저장을못하는 문제는 해결이 되지않음 단일서버에서 어떻게 더좋게 
    개선할수있는지 고민을 해본결과 스케일업을 제외하고는 실시간채팅에 요구사항에 따라 데이터베이스 메시지패턴등을 좀더 최적화
    할수밖에없는데 이미 1차 2차개선으로 단일서버에서 할수있을만큼 모든처리는 다했음 같은 코드로 분산처리를 도입하여 시도해볼생각
 정리하도록 하자

### 결론
- 두 차례의 개선으로 실시간 채팅의 처리량과 안정성이 크게 향상되었습니다.
- **병렬 스트림**과 **리액티브 프로그래밍**을 통해 대량 트래픽에서도 효율적인 메시지 처리와 빠른 응답을 달성하였습니다.
- **향후 계획** 
  - 도커 컨테이너에 메모리 제한을 설정하여 분산 처리 환경에서 성능 개선 효과를 테스트할 예정입니다.  
  이를 통해 대규모 트래픽 처리 시 성능 최적화 방안을 도출하고, 단일 서버의 한계를 극복하기 위한 **분산 처리 전략**을 검증할 계획입니다.

---
## 트러블 슈팅

---
## 기술적 의사결정


---
## 프로젝트 개선 방향 및 추후 계획

### 뉴스피드
- **사용자 프로필 기반 추천**  
  나이, 지역, 관심사 등 프로필 데이터를 반영하여 개인화된 추천 알고리즘을 구현할 계획입니다.
- **히스토리 기반 추천**  
  사용자의 과거 상호작용 데이터를 분석하여 선호도에 맞는 콘텐츠를 추천합니다.
- **실시간 트렌드 반영**  
  특정 시간대의 인기 콘텐츠와 트렌드를 분석해 동적으로 추천하는 시스템을 도입할 예정입니다.

### 주식 가상 매도/매수 기능
- **학습용 거래 환경**  
  실제 돈 거래는 지원하지 않으며, 가상의 거래 데이터를 통해 성과를 기록하고 저장할 수 있는 기능을 추가할 계획입니다.

### 실시간 채팅
- **분산 처리로 확장 예정**  
  단일 서버의 성능 한계를 극복하기 위해 분산 시스템 설계를 도입합니다.
- **메시지 브로커 활용**  
  Kafka 등 메시지 브로커를 통해 메시징 시스템을 최적화하고 확장 가능성을 확보할 예정입니다.

### 회복탄력성을 위한 Resilience4j
- **Circuit Breaker**  
  장애 발생 시 특정 서비스로의 요청을 차단하여 시스템 안정성을 유지합니다.
- **Retry**  
  실패한 요청에 대한 재시도 전략을 적용하여 네트워크 오류와 지연 문제를 완화할 계획입니다.

### API Gateway 및 보안
- **API Gateway**  
  요청 검증과 속도 제한(Rate Limiting)을 통해 트래픽 관리 체계를 강화하고, 보안 사고를 예방하는 구조를 구축할 예정입니다.
- **토큰 기반 인증 고도화**  
  JWT를 활용하여 짧은 유효 기간과 리프레시 토큰을 적용하며, 스코프 기반 권한 관리로 보안을 강화합니다.
- **OAuth 서버 도입 검토**  
  중앙 Auth 서버를 구축하여 인증과 권한 관리를 표준화하고 관리 효율성을 높이는 방안을 고려 중입니다.  


