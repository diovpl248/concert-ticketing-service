# 아키텍처 설계서 (System Architecture Design)

## 1. 시스템 아키텍처 (System Architecture)

```mermaid
graph TD
    User["Client (Nuxt 4)"]
    LB["Load Balancer (L7 Routing)"]
    QueueApi["Queue API Server (Spring Boot)"]
    CoreApi["Core API Server (Spring Boot)"]
    Redis["Redis Cluster"]
    DB[("MySQL")]

    %% Traffic Flow
    User -->|HTTPS| LB
    LB -->|"Path: /queue/*"| QueueApi
    LB -->|"Path: /api/*"| CoreApi
    
    %% Backend Interactions
    QueueApi <-->|"Queue Operations (ZSET)"| Redis
    CoreApi <-->|"Lock & Cache"| Redis
    CoreApi -->|"Transactional Data"| DB
    
    %% Internal Communication (Optional)
    QueueApi -.->|"Token Verification (gRPC/HTTP)"| CoreApi
```

---

## 2. 프로세스 흐름도 (Sequence Diagrams)

### 2.1. 대기열 진입 및 토큰 발급 (Queue Flow)

```mermaid
sequenceDiagram
    participant U as User
    participant Q as Queue API Server
    participant R as Redis

    U->>Q: 접속 요청 (GET /queue/status)
    Q->>R: 대기열 상태 확인 (ZCard)
    
    alt 대기열 없음 (여유)
        R-->>Q: Count < Threshold
        Q-->>U: "바로 입장 가능" (Token 발급)
    else 대기열 있음 (혼잡)
        Q->>R: 대기열 진입 (ZAdd)
        R-->>Q: OK
        Q-->>U: "대기 중" (Rank N)
        
        loop Polling (매 3s)
            U->>Q: 내 순서 확인
            Q->>R: 순번 조회 (ZRank)
            R-->>Q: Rank
            Q-->>U: 남은 인원 & 예상 시간
        end
        
        Note over U, Q: 순번 도래 시
        Q-->>U: Active Token 발급
    end
```

### 2.2. 좌석 예매 (Booking Flow)

```mermaid
sequenceDiagram
    participant U as User
    participant C as Core API Server
    participant R as Redis
    participant D as MySQL

    U->>C: 공연장 진입 (GET /api/concerts)
    C->>R: 토큰 유효성 검증 (Active Token?)
    alt Invalid Token
        C-->>U: 대기열로 리다이렉트
    else Valid Token
        C->>D: 공연 목록 조회
        D-->>C: Result
        C-->>U: Show List
    end

    U->>C: 좌석 선택 (POST /api/bookings)
    C->>R: Try Lock (Seat ID)
    alt Lock Fail
        C-->>U: "이미 선택된 좌석"
    else Lock Success
        C->>D: 좌석 상태 조회 & 임시 배정
        D-->>C: Success
        C-->>U: 예매 상태 (결제 대기)
    end
```

### 2.3. 결제 (Payment Flow)

```mermaid
sequenceDiagram
    participant U as User
    participant C as Core API Server
    participant D as MySQL
    participant R as Redis

    U->>C: 결제 요청 (POST /api/payments)
    C->>D: 예매 건 조회 (Status: Holding?)
    alt Expired or Invalid
        C-->>U: "결제 시간 초과/유효하지 않음"
    else Valid
        C->>C: Payment Processing (Dummy Lgoic)
        alt Pay Success
            C->>D: 예매 확정 (Status: Paid)
            C->>R: 좌석 Lock 영구 점유 or 해제 후 상태 변경
            C->>U: 예매 완료 (Ticket Issued)
        else Pay Fail
            C->>D: 상태 복구 (Status: Available)
            C->>R: Release Seat Lock
            C-->>U: 결제 실패 알림
        end
    end
```

---

## 3. 데이터베이스 설계 (ERD)

```mermaid
erDiagram
    USERS {
        bigint id PK
        string name
        string email
        datetime created_at
    }
    CONCERTS {
        bigint id PK
        string title
        text description
        datetime created_at
    }
    CONCERT_SCHEDULES {
        bigint id PK
        bigint concert_id FK
        datetime perform_date
        int round
        datetime created_at
    }
    SEATS {
        bigint id PK
        bigint schedule_id FK
        string seat_no
        string status "AVAILABLE, HELD, BOOKED"
        decimal price
        bigint version "Optimistic Lock용"
    }
    BOOKINGS {
        bigint id PK
        bigint user_id FK
        bigint seat_id FK
        string status "PENDING, PAID, CANCELLED"
        datetime created_at
        datetime expired_at
    }

    USERS ||--o{ BOOKINGS : makes
    CONCERTS ||--o{ CONCERT_SCHEDULES : has
    CONCERT_SCHEDULES ||--o{ SEATS : contains
    SEATS ||--o{ BOOKINGS : associated_with
```

### 3.1. 주요 컬럼 및 상태 설명 (Schema Details)

*   **`SEATS.version` (낙관적 락)**:
    - **목적**: 동시성 제어를 위한 버전 관리 컬럼.
    - **내용**: 다수의 사용자가 동시에 같은 좌석을 선점하려 할 때, `UPDATE ... WHERE version={ver}` 쿼리를 통해 최초의 요청만 성공시키고 나머지는 실패하도록 보장합니다.

*   **`SEATS.status` (좌석 상태)**:
    - **AVAILABLE**: 예약 가능한 빈 좌석.
    - **HELD (임시 배정)**: 사용자가 "좌석 선택"을 완료하고 **결제 대기 중**인 상태. (타임아웃 5~10분 적용)
    - **BOOKED (예약 확정)**: 결제가 완료되어 최종적으로 소유권이 넘어간 상태.

### 3.2. Redis 데이터 설계 및 접근 패턴 (Redis Data Design)

| Key Pattern | Type | Expiration (TTL) | 설명 (Purpose) | Access Pattern (Command) |
| :--- | :--- | :--- | :--- | :--- |
| `queue:wait:{concertId}` | Sorted Set | - | 대기열 (점수=타임스탬프) | `ZADD` (입장), `ZRANK` (순번 조회), `ZPOPMIN` (토큰 발급) |
| `queue:active:{concertId}` | Set | 10분~30분 | 입장 완료 토큰 (유효성 검증용) | `SADD` (토큰 활성화), `SISMEMBER` (유효성 체크), `SREM` (만료/퇴장) |
| `seat:lock:{seatId}` | String | 5분 | 좌석 선점 Lock (Value=UserId) | `SETNX` (락 획득), `GET` (소유자 확인), `DEL` (해제) |

#### 💡 접근 패턴 (Access Patterns)
1.  **대기열 진입**: `ZADD queue:wait:1 {timestamp} {userId}`
2.  **순번 조회**: `ZRANK queue:wait:1 {userId}`
3.  **토큰 활성화 (N명 입장)**:
    - `ZPOPMIN queue:wait:1 {N}` -> `SADD queue:active:1 {userId...}`
4.  **토큰 검증**: `SISMEMBER queue:active:1 {userId}` -> `True/False`
5.  **좌석 선점**: `SET seat:lock:5A-13 user_123 NX EX 300` (5분 TTL, 성공 시 1 반환)

---

## 4. 프로젝트 폴더 구조 (Project Directory Structure)

### 4.1. Frontend (Nuxt 4 Structure)
```
frontend/
├── app/                    # Nuxt 4 Application Directory
│   ├── assets/             # Global Assets (CSS, Fonts)
│   ├── components/         # Auto-imported Components
│   ├── composables/        # Auto-imported Composables
│   ├── layouts/            # Layouts
│   ├── pages/              # File-based Routing
│   │   ├── index.vue
│   │   ├── concerts/
│   │   │   ├── index.vue
│   │   │   └── [id].vue
│   │   └── booking/
│   ├── plugins/            # Plugins
│   ├── utils/              # Utils
│   ├── app.vue             # Root Component
│   └── router.options.ts   # Router Options
├── public/                 # Static Files
├── server/                 # Server Routes (Nitro)
├── stores/                 # Pinia Stores (Optional: can be in app/stores)
├── nuxt.config.ts          # Nuxt Configuration
└── package.json
```

### 4.2. Backend (Spring Boot 3 - Multi Module)
```
backend/
├── build.gradle        # Root Gradle
├── settings.gradle
├── ticket-core/        # Shared Domain, Entities, Utils
│   └── src/main/java/com/ticket/core/
│       ├── domain/
│       └── infrastructure/
├── ticket-queue-api/   # Queue Server (Redis Heavy)
│   └── src/main/java/com/ticket/queue/
│       └── controller/QueueController.java
└── ticket-app-api/     # Core Business Server (MySQL Heavy)
    └── src/main/java/com/ticket/api/
        ├── controller/
        └── service/
```

---

## 5. 기술적 트레이드오프: 대기열 아키텍처 (Technical Trade-offs: Queue Build Strategy)

| 방식 | 1. 단일 API 서버 (Monolithic) | 2. 별도 대기열 서버 분리 (Dedicated Queue Server) |
| :--- | :--- | :--- |
| **구조** | 하나의 서버에서 대기열 진입, 조회, 메인 비즈니스 로직을 모두 처리. | 대기열 처리 전용 서버와 메인 비즈니스 서버를 물리적/논리적으로 분리. |
| **장점** | - **구현 단순성**: 프로젝트 구조가 하나라 개발/배포가 간편.<br>- **비용 절감**: 인프라 리소스가 적게 듬. | - **장애 격리**: 대기열 트래픽 폭주로 서버가 다운되어도 이미 입장한 유저의 결제/예매는 안전.<br>- **스케일링 유연성**: Redis I/O 중심인 큐 서버와 CPU/DB 중심인 API 서버를 각각 최적화 가능. |
| **단점** | - **장애 전파**: 대기열 트래픽(단순 조회)이 폭주하면 CPU/Memory를 점유하여 결제 등 핵심 로직까지 마비됨.<br>- **확장성 한계**: 대기열만 늘리고 싶은데 전체 서버를 증설해야 함. | - **운영 복잡도**: 배포 단위가 2개가 되며, 관리 포인트 증가.<br>- **통신 비용**: 서버 간 통신이 필요한 경우 로직이 복잡해질 수 있음 (본 설계에서는 DB/Redis 공유로 해결). |
| **권장** | | **✅ 별도 대기열 서버 분리** |
| **이유** | 10만 명의 대기열 트래픽은 단순 조회(Polling)가 주를 이루므로, 이를 메인 비즈니스 로직과 섞으면 핵심 트랜잭션(결제, 예매)의 안정성을 보장하기 어려움. 따라서 안정성을 최우선으로 하여 역할을 분리하는 것이 바람직함. |

---


