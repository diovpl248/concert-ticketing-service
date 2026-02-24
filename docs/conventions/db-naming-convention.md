# 데이터베이스 네이밍 컨벤션 (Database Naming Conventions)

본 프로젝트의 데이터베이스 설계 및 스키마 작성 시 적용되는 명명 규칙(Naming Convention)입니다. 일관성 있는 데이터베이스 관리를 위해 아래의 규칙을 준수합니다.

## 1. 기본 원칙 (General Rules)

* **언어**: 영어 (English)
* **표기법**: 스네이크 케이스 (snake_case)
    * 모든 이름은 소문자로 작성하며, 단어와 단어 사이는 언더스코어(`_`)로 구분합니다.
    * 예: `user_id`, `concert_date`
* **명확성**: 축약어 사용을 지양하고, 누구나 의미를 명확히 알 수 있는 단어를 사용합니다. (널리 통용되는 `id` 등의 예외 제외)

## 2. 테이블 (Tables)

* **복수형 명사** 사용: 데이터의 집합을 나타내므로 복수형으로 명명합니다.
    * 예: `users`, `concerts`, `bookings`, `concert_dates`
* 다대다(M:N) 관계 매핑 테이블: 두 테이블의 이름을 언더스코어로 연결하며, 알파벳 순서 또는 논리적 주/종 관계에 따라 명명합니다. (현재 모델에는 없음)

## 3. 컬럼 (Columns)

* **단수형 명사** 사용: 각 속성을 나타내므로 단수형으로 명명합니다.
* **PK (Primary Key)**: 기본키는 무조건 `id` (단독)로 명명합니다. (Auto Increment `BIGINT` 타입 권장)
* **FK (Foreign Key)**: 외래키는 식별하기 쉽도록 참조하는 `{단수형_테이블명}_id` 형식으로 명명합니다.
    * 예: `user_id`, `concert_id`, `concert_date_id`
* **Boolean (논리형) 컬럼**: `is_`, `has_` 와 같이 접두사를 사용하여 상태나 여부를 명확히 나타내도록 권장합니다. (미니멀 스키마에서는 Boolean 대신 Status Enum 문자열을 사용)
* **Status (상태) 컬럼**: `status` 라는 명칭을 사용하고, 애플리케이션 레벨의 로직 처리를 단순화하기 위해 가급적 문자열(VARCHAR)을 사용합니다.
    * 예: `status` (값: `AVAILABLE`, `HELD`, `BOOKED` 등)
* **날짜 및 시간**:
    * 날짜: `_date` 접미사를 사용합니다. (예: `concert_date`)
    * 시간: `_time` 접미사를 사용합니다. (예: `concert_time`)
    * 일시(Timestamp): `_at` 접미사를 사용합니다. (예: `expires_at`)

## 4. 인덱스 및 제약 조건 (Indexes & Constraints)

일관된 관리를 위해 명확한 접두사(Prefix) 형태의 명명 규칙을 적용합니다.

* **Primary Key (PK)**:
    * 형식: `pk_{table_name}`
    * (대부분의 RDBMS에서 자동 생성되므로 명시할 필요가 없는 경우가 많습니다.)
* **Foreign Key (FK)**:
    * 형식: `fk_{table_name}_{column_name}`
    * (현재 요구사항에서는 성능(Insert/Update 속도) 및 Deadlock 회피를 위해 물리적인 FK 제약을 설정하지 않고, 애플리케이션 레벨에서 무결성을 검증하며 인덱스만 생성합니다.)
* **일반 인덱스 (Index)**:
    * 형식: `idx_{column_name}`
    * 복합 인덱스의 경우: `idx_{column1}_{column2}`
    * 예: `idx_concert_id`, `idx_user_id`
* **고유 키 제약조건 / 유니크 인덱스 (Unique Index)**:
    * 형식: `ux_{column_name}`
    * 일반 인덱스(`idx_`)와의 구분을 위해 `ux_` 접두사를 사용합니다.
    * 복합 고유 키의 경우: `ux_{concept_name}` 또는 `ux_{column1}_{column2}`
    * 예: `ux_seat_position` (concert_date_id, seat_no 조합), `ux_seat_id`
