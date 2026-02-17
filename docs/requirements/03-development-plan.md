# 개발 계획 및 마일스톤 (Development Plan)

## 📅 전체 일정 및 단계 (Phrases)

### Phase 1. 환경 설정 및 기초 공사 (Environment Setup)
- [ ] **프로젝트 생성** (Nuxt 4 + Spring Boot)
- [ ] **Docker Compose 구성** (MySQL, Redis Cluster)
- [x] **Git Repository 초기화**

### Phase 2. 프론트엔드 화면 설계 및 프로토타입 (Frontend Screen Design)
- [ ] **Nuxt 4 프로젝트 기본 설정** (Layout, Tailwind, Pinia)
- [ ] **화면/컴포넌트 구현 (Mock Data 기반)**
    - [ ] 대기열 페이지 (Polling UI)
    - [ ] 콘서트 목록/상세 페이지
    - [ ] 좌석 선택 (SVG/Canvas)
    - [ ] 결제 페이지
- [ ] **API 인터페이스 도출** (화면에 필요한 데이터 구조 정의 -> API 스펙 설계)

### Phase 3. API 설계 및 Core 구현 (API Design & Core Implementation)
- [ ] **API 스펙 정의** (Swagger/OpenAPI - Phase 2 기반)
- [ ] **Entity 설계 및 구현** (User, Concert, Seat, Booking)
- [ ] **Repository 인터페이스 정의**
- [ ] **공통 모듈 구현** (Exception Handling, Response DTO)

### Phase 4. 대기열 시스템 구현 (Queue System Implementation)
- [ ] **Redis Repository 구현**
- [ ] **대기열 토큰 발급/검증 로직**
- [ ] **스케줄러 구현**

### Phase 5. 비즈니스 로직 구현 (Business Logic Implementation)
- [ ] **API 구현 (Controller, Service)**
    - [ ] 콘서트/좌석 조회
    - [ ] 좌석 예약 (분산 락 적용)
    - [ ] 결제 프로세스
- [ ] **프론트엔드 연동** (Mock Data -> Real API)

### Phase 6. 최종 점검 및 최적화 (Optimization)
- [ ] **E2E 테스트** (대기열 -> 예약 -> 결제 시나리오)
- [ ] **부하 테스트** (k6 / JMeter - 대기열 진입 10만 RPS 시뮬레이션)
- [ ] **쿼리 튜닝 및 인덱스 점검**
