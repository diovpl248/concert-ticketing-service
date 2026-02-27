# API 명세서 (API Specifications)

## 1. 공통 사항 (Common)
- **Response Format**: JSON
- **Error Format**:
  ```json
  {
    "code": "ERROR_CODE",
    "message": "Error description"
  }
  ```

---

## 2. 대기열 API (Queue API) - Flow Step 2~3

### 2.1. 대기열 진입 및 토큰 발급
- **POST** `/queue/tokens`
- **Header**: `Authorization: Bearer {jwt_token}`
- **Body**: `{ "concertId": 1 }`
- **Description**: 대기열에 진입하고 토큰을 발급받습니다. 대기열 상태에 따라 WAITING 또는 ACTIVE 상태가 반환됩니다.
- **Response**:
  ```json
  {
    "token": "uuid-token-value",
    "status": "WAITING", // or ACTIVE (대기 없음)
    "position": 150, // 대기 순서 (ACTIVE인 경우 0)
    "estimatedWaitTime": 300 // 초 단위 (ACTIVE인 경우 0)
  }
  ```

### 2.2. 대기열 상태 확인 (Polling)
- **GET** `/queue/status?concertId={concertId}`
- **Header**: `Queue-Token: {token}`
- **Description**: 현재 대기 상태를 확인합니다. 주기적으로 호출하여 자신의 순서를 확인합니다.
- **Response**:
  ```json
  {
    "token": "uuid-token-value",
    "status": "WAITING", // WAITING, ACTIVE, EXPIRED
    "position": 140, // 남은 대기 순서
    "estimatedWaitTime": 280
  }
  ```

---

## 3. 공연 API (Concert API) - Flow Step 1 & 4

### 3.1. 공연 목록 조회
- **GET** `/api/concerts`
- **Description**: 메인 페이지 등에서 확인할 수 있는 공연 목록을 조회합니다.
- **Response**:
  ```json
  [
    {
      "id": 1,
      "title": "네온 나이츠 월드 투어 2024",
      "category": "CONCERT", // or MUSICAL, CLASSIC
      "thumbnailUrl": "https://...",
      "date": "2024-10-24",
      "venue": "서울 잠실 올림픽 주경기장",
      "status": "OPEN"
    }
  ]
  ```

### 3.2. 공연 상세 조회
- **GET** `/api/concerts/{concertId}`
- **Description**: 공연의 상세 정보와 예매 가능한 날짜/회차 정보를 조회합니다.
- **Response**:
  ```json
  {
    "id": 1,
    "title": "네온 나이츠 월드 투어 2024",
    "category": "CONCERT",
    "description": "...",
    "venue": "서울 잠실 올림픽 주경기장",
    "dates": [
       { "id": 101, "date": "2024-10-24", "time": "19:00" },
       { "id": 102, "date": "2024-10-25", "time": "19:00" }
    ]
  }
  ```

### 3.3. 좌석 조회 (Requires Active Token)
- **GET** `/api/concerts/{concertId}/dates/{dateId}/seats`
- **Header**: `Queue-Token: {active_token}`
- **Description**: 예약 가능한 좌석 정보를 조회합니다. **유효한 Active 토큰**이 헤더에 포함되어야 합니다.
- **Response**:
  ```json
  {
    "concertId": 1,
    "dateId": 101,
    "seats": [
      { "id": 1, "section": "VIP", "row": "A", "col": "1", "status": "AVAILABLE", "price": 150000 },
      { "id": 2, "section": "VIP", "row": "A", "col": "2", "status": "BOOKED", "price": 150000 },
      { "id": 3, "section": "VIP", "row": "A", "col": "3", "status": "HELD", "price": 150000 }
    ]
  }
  ```

---

## 4. 예약 API (Booking API) - Flow Step 4

### 4.1. 좌석 임시 배정 (좌석 선택)
- **POST** `/api/bookings`
- **Header**: `Queue-Token: {active_token}`
- **Body**:
  ```json
  {
    "seatId": 1,
    "concertId": 1,
    "dateId": 101
  }
  ```
- **Description**: 좌석을 선택하여 **임시 배정(HELD)** 상태로 만듭니다. 성공 시 결제 페이지로 이동합니다.
- **Response**:
  ```json
  {
    "bookingId": 5001,
    "status": "HELD",
    "expiresAt": "2024-10-24T10:05:00Z"
  }
  ```

---

## 5. 결제 API (Payment API) - Flow Step 5

### 5.1. 결제 요청 및 처리
- **POST** `/api/payments`
- **Header**: `Queue-Token: {active_token}`, `Authorization: Bearer {jwt_token}`
- **Body**:
  ```json
  {
    "bookingId": 5001,
    "amount": 150000,
    "paymentMethod": "CARD"
  }
  ```
- **Description**: 임시 배정된 예약을 확정(PAID)하고, 대기열 토큰을 만료 처리합니다.
- **Response**:
  ```json
  {
    "paymentId": 9001,
    "status": "PAID",
    "bookingId": 5001,
    "ticketCode": "T-20241024-8842",
    "issuedAt": "2024-10-24T10:02:00Z"
  }
  ```
