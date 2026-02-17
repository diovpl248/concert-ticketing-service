# 대기열이 있는 티켓팅 시스템 요구사항 정의서 (Ticketing System Requirements)
## 1. 개요 (Project Overview)
- 본 프로젝트는 **대규모 트래픽(10만 명 가정)** 을 효과적으로 처리하기 위한 **대기열(Waiting Queue)** 시스템이 포함된 공연 예매 서비스를 구축하는 것을 목표로 합니다.
- AWS 클라우드 환경에서 운영되지만 특정 벤더의 종속성(Lock-in)을 배제한 아키텍처를 지향합니다.
---
## 2. 기술 스택 (Tech Stack)
### Frontend
- **Framework**: Nuxt 4 (Vue 3 Composition API)
- **State Management**: Pinia
- **Styling**: Tailwind CSS, SCSS
- **Language**: TypeScript
### Backend
- **Framework**: Spring Boot 3 (Java)
- **Database**: 
  - **RDBMS**: MySQL (공연 정보, 예매 내역, 사용자 정보 등 영구 저장)
  - **In-Memory**: Redis (대기열 관리, 세션, 캐싱, 분산 락)
- **Language**: Java 21
### Infrastructure & Operations
- **Cloud Provider**: AWS (EC2/EKS/ECS, RDS, ElastiCache 활용)
- **Constraints**: 
  - AWS Lambda, DynamoDB 등 벤더 전용(Proprietary) 서비스 사용 배제.
  - 표준 프로토콜 및 오픈소스 호환성 유지 (Docker 컨테이너 기반 배포 지향).
---
## 3. 핵심 기능 요구사항 (Core Features)
### 3.1. 대기열 시스템 (Traffic Control & Waiting Queue)
- **목적**: 백엔드 서버의 과부하를 방지하고 트래픽을 순차적으로 처리.
- **구현 방식**:
  - Redis의 `Sorted Set` 또는 `List` 자료구조를 활용하여 진입 순서 관리.
  - 사용자는 최초 접속 시 대기열 토큰(Token) 발급.
  - **Flow**:
    1. 사용자 접속 요청.
    2. 현재 처리량(Throughput) 확인 후 여유 있으면 통과, 아니면 대기열 진입.
    3. 대기열 진입 시 자신의 대기 순번 및 예상 대기 시간 안내.
    4. 순번 도래 시 유효 토큰(Access Token) 발급 및 메인 서비스 리다이렉트.
   - **Redis Sorted Set 상세 구조 (Technical Detail)**:
     - **Key**: `waiting_queue:{eventId}`
     - **Value**: `userId` (또는 uniqueToken)
     - **Score**: Unix Timestamp (진입 시각, 밀리초 단위)
     - **성능 분석**:
       - Redis의 Sorted Set은 내부적으로 **Skip List** 자료구조를 사용하여 삽입/검색 시 **O(log N)**의 시간 복잡도를 가짐.
       - **10만 명 저장 시**: 100,000명의 유저 아이디(UUID 등)라도 메모리는 수십 MB 수준으로 매우 적으며, 삽입 시 약 17번의 비교 연산만 수행하므로 병목 없이 처리가 가능함.
       - 단일 Key에 10만 개 데이터를 넣는 것은 Redis 성능상 전혀 문제가 되지 않음 (Redis 단일 Key 최대 저장 가능 개수: 약 40억 개).
   - **확장성 고려 (Future Scalability)**:
     - **단일 Key (현재 방식)**: 10만~100만 건까지는 단일 Key로 충분히 처리가 가능함.
     - **Sharding (미래 확장안)**: 만약 대기 인원이 수백만 명을 넘어가 단일 Key의 연산 부하가 커질 경우, Key를 분산(Sharding)하여 처리하는 방식(`waiting_queue:1`, `waiting_queue:2`)을 고려할 수 있음. 하지만 본 프로젝트 스펙(10만 명)에서는 오버 엔지니어링(Over-engineering)이므로 단일 Key 방식을 채택.
### 3.2. 공연 예매 (Concert Booking)
- **공연 목록 및 회차**:
  - 공연 리스트 조회.
  - 날짜 및 회차 선택.
- **좌석 선택 (Seat Selection)**:
  - 실시간 좌석 상태(예매 가능, 선택 중, 예매 완료) 표시.
  - **동시성 제어 (Concurrency Control)**:
    - 다수의 사용자가 동시에 같은 좌석을 선택할 경우, 먼저 선점한 사용자에게 우선권 부여.
    - Redis Distributed Lock(Redisson 등) 또는 DB Row Lock 활용.
### 3.3. 회원 및 결제 (User & Payment) - Dummy Services
- **로그인**:
  - 복잡한 인증 절차 없이, 사용자 식별을 위한 최소한의 더미 로그인 기능 제공.
  - (예: ID 입력 시 자동 로그인 처리 등 간소화)
- **결제**:
  - PG사 연동 없이 결제 성공/실패 시나리오를 시뮬레이션하는 더미 결제 모듈.
  - 결제 승인 시 예매 확정, 실패 또는 타임아웃 시 좌석 점유 해제.
---
## 4. 페이지 구성 (Sitemap)
1. **대기열 페이지 (Landing / Queue Page)**
   - 접속 시 트래픽이 많을 경우 가장 먼저 보여지는 페이지.
   - "현재 대기 인원: N명", "예상 입장 시간: M분" 등의 정보 표시.
   - 주기적 폴링(Polling) 또는 소켓(WebSocket)으로 상태 갱신.
2. **메인 / 공연 목록 (Concert List)**
   - 대기열 통과 후 진입하는 메인 화면.
   - 예매 가능한 공연 리스트 표시.
3. **공연 상세 및 회차 선택 (Concert Detail)**
   - 선택한 공연의 상세 정보.
   - 날짜 및 회차(Round) 선택 캘린더/리스트.
4. **좌석 선택 (Seat Map)**
   - 구역 및 좌석 배치도 시각화.
   - 좌석 클릭 시 '임시 선점(Hold)' 상태로 전환.
5. **로그인 (Login Modal/Page)**
   - 예매 진행 전 또는 좌석 선택 직전 로그인 요구 (더미).
6. **결제 (Payment Page)**
   - 예매 정보 확인 및 결제 진행 버튼.
   - 성공/실패 시뮬레이션 옵션 제공 가능.
7. **예매 완료 (Booking Complete)**
   - 예약 번호 및 예매 상세 내역 표시.
---
## 5. 데이터베이스 설계 방향 (Database Schema Draft)
### MySQL (RDBMS)
- **Users**: 사용자 정보 (ID, Name, etc.)
- **Concerts**: 공연 정보 (Title, Description, etc.)
- **ConcertSchedules**: 공연 일정 (Date, Time, Round)
- **Seats**: 좌석 정보 (ScheduleID, SeatNumber, Status, Price)
- **Bookings**: 예매 내역 (UserID, SeatID, Status, PaymentStatus, Timestamp)
### Redis (NoSQL)
- **Waiting Queue**: `waiting_queue` (Sorted Set - Timestamp 점수 기반)
- **Active Tokens**: `active_tokens` (Set - 입장 허용된 토큰 목록)
- **Seat Locks**: `seat_lock:{scheduleId}:{seatNo}` (Key-Value - 좌석 선점 락, TTL 설정 필수)
---
## 6. 비기능 요구사항 (Non-Functional Requirements)
- **확장성**: 트래픽 증가 시 수평 확장(Scale-out)이 용이한 구조.
- **안정성**: 대기열 시스템이 다운되더라도 메인 서비스에 영향을 최소화하거나, 반대로 메인 서비스 장애 시 대기열에서 적절히 차단.
- **성능 목표**:
  - 동시 접속자 10만 명 대기열 처리.
  - 예매 트랜잭션의 데이터 정합성 보장 (Overbooking 방지).
---
## 7. 기술적 의사결정 및 트레이드오프 (Technical Trade-offs)
### 7.1. 대기열 관리 (Waiting Queue Management)
| 방식 | Redis (Sorted Set) | RDBMS (Table) | In-App Memory |
| :--- | :--- | :--- | :--- |
| **장점** | - **고성능**: 인메모리 기반으로 읽기/쓰기 속도가 매우 빠름.<br>- **순서 보장**: Sorted Set으로 대기 순번 및 시간 계산 용이.<br>- **TTL**: 자동 만료 기능으로 토큰 관리 용이. | - **데이터 무결성**: ACID 트랜잭션 보장.<br>- **구현 용이성**: 기존 RDBMS 활용 가능. | - **최고 성능**: 네트워크 통신 비용 없음. |
| **단점** | - **운영 복잡도**: 별도 인프라 구축 및 관리 필요.<br>- **데이터 유실 가능성**: 장애 시 일부 데이터 유실 가능 (AOF/RDB 보완). | - **성능 저하**: 대규모 트래픽 시 DB 부하 집중 (I/O Bottleneck).<br>- **Lock 경합**: 잦은 Insert/Delete로 인한 성능 저하. | - **확장성 부족**: 서버 다중화 시 대기열 동기화 어려움.<br>- **휘발성**: 서버 재시작 시 대기열 초기화. |
| **선택** | **✅ Redis (Sorted Set)** | | |
| **이유** | 10만 명 이상의 대규모 트래픽 처리가 필요하며, 빠른 응답 속도와 순차 처리가 핵심 요구사항임. RDBMS는 부하를 견디기 어렵고, In-Memory는 확장에 제한이 있어 Redis가 최적의 선택. | | |
### 7.2. 좌석 예약 동시성 제어 (Concurrency Control)
| 방식 | Redis Distributed Lock (Redisson) | DB Pessimistic Lock (Bi-ta) | DB Optimistic Lock (Nak-gwan) |
| :--- | :--- | :--- | :--- |
| **장점** | - **DB 부하 감소**: 락 획득 대기를 Redis에서 처리하여 DB 커넥션 점유 최소화.<br>- **기능**: 타임아웃, 재시도 로직(Spin Lock) 등 고급 기능 지원 (Redisson Pub/Sub). | - **확실한 데이터 보호**: DB 레벨에서 강력한 정합성 보장 (`SELECT FOR UPDATE`). | - **No Login Wait**: 락을 걸지 않아 리소스 점유 없음. |
| **단점** | - **구현 복잡도**: 별도 락 관리 로직 및 Redis-DB 간 정합성 관리 필요. | - **성능 저하**: 트랜잭션 대기 시간 증가로 인한 전체 처리량 감소.<br>- **Deadlock**: 교착 상태 발생 위험 높음. | - **높은 재시도율**: 충돌 발생 시 재시도해야 하므로, 선착순 예매 특성상 사용자 경험 저하 (잦은 실패). |
| **선택** | **✅ Redis Distributed Lock (Redisson)** | | |
| **이유** | 좌석 선점은 짧은 시간 동안 많은 요청이 몰리는 'Hot Spot' 문제임. DB 락은 성능 저하를 유발할 수 있으며, 빠른 응답과 DB 보호를 위해 Redis 분산 락을 사용하여 앞단에서 제어하는 것이 효과적. | | |
