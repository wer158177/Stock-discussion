

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

  <table>
    <tr>
      <td><img src="https://github.com/user-attachments/assets/bb9917ff-8229-44b9-b37e-e2c3b52f792d" width="400"/></td>
      <td><img src="https://github.com/user-attachments/assets/d7b33490-0d20-4e8f-8a10-18f5843579fa" width="400"/></td>
    </tr>
    <tr>
      <td><img src="https://github.com/user-attachments/assets/c61dbaa0-0409-463e-b120-88e5e9087fac" width="400"/></td>
      <td><img src="https://github.com/user-attachments/assets/9ea67c90-23b3-4146-9b40-9e5017f68ad9" width="400"/></td>
    </tr>
  </table>

</details>



<details>
  <summary><span style="font-size: 21px;">📂 System Architecture</summary>
  <img src="https://github.com/user-attachments/assets/3bff9c68-7667-4606-9f19-f7ebdca59a20" alt="Architecture">
</details>

<details>
  <summary><span style="font-size: 21px;">📊 ERD</summary>
  <img src="https://github.com/user-attachments/assets/4091b794-ef9b-4a23-8ac9-4d10908863e3" alt="ERD">
</details>

<details>
  <summary><span style="font-size: 21px;">📋 API 문서</summary>
  포스트맨으로 작성 예정
</details>

<details>
  <summary><span style="font-size: 21px;">🛠️ 기술 스택</summary>

### **Backend**
Spring Boot, Spring Cloud Gateway, Eureka, OpenFeign, Spring Security, Spring WebFlux, Apache Kafka  

### **Database**
MySQL, PostgreSQL, Redis (캐싱)  

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

### 유저 활동 기록 및 알람 생성

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/9c414731-4b27-44e8-aa22-78ed6b9f998e" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/6e831e47-5cd4-42c5-a7d4-31ba5fe8d00a" width="400"/></td>
  </tr>
</table>

### 유저 활동 기록을 기반으로 한 알림 프로세스 요약 및 기능 플로우

- **활동 기록 생성**: 사용자가 게시글을 작성, 팔로우 요청 등  서버가가 이벤트를 발행하고, ActivityService가 이를 구독하여 유저 활동을 기록.
- **실시간 알림 전송**: 기록된 활동 정보를 기반으로 알림을 생성하고, **SSE(Server-Sent Events)**를 통해 실시간으로 사용자에게 전송.
- **알림 저장**: 생성된 알림은 데이터베이스에 저장되어 관리.


---


## 프로젝트 개선 방향 및 추후 방향




