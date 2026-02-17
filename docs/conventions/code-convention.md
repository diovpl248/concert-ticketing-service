# 코딩 컨벤션 (Code Convention)

## 1. 기본 원칙 (Principles)
*   **Follow SOLID Principles**: 객체 지향 5대 원칙(SRP, OCP, LSP, ISP, DIP)을 준수할 것.
*   **Don't Repeat Yourself (DRY)**: 중복 코드를 만들지 말 것.
*   **Keep It Simple, Stupid (KISS)**: 코드는 최대한 단순하고 명확하게 유지할 것.
*   **You Ain't Gonna Need It (YAGNI)**: 당장 필요하지 않은 기능은 구현하지 말 것.
*   **Boy Scout Rule**: 처음 발견했을 때보다 더 깨끗하게 코드를 리팩토링하고 떠날 것.

## 2. 클린 코드 (Clean Code)
*   **Meaningful Names**: 변수/함수/클래스 이름은 의도를 분명히 밝힐 것.
*   **Functions**:
    *   **One Thing**: 함수는 한 가지 일만 할 것.
    *   **Small**: 함수는 작을수록 좋음 (20줄 이내 권장).
    *   **Args**: 인수는 적을수록 좋음 (0~2개 권장, 많으면 객체 사용).
*   **Comments**:
    *   **Do Not Explain Code**: 나쁜 코드를 주석으로 설명하지 말고, 코드를 개선할 것.
    *   **Why**: 코드로 표현할 수 없는 '의도'와 '이유'만 작성할 것.
*   **Error Handling**:
    *   **Prefer Exceptions**: 에러 코드 확인보다 예외 처리를 선호할 것.
    *   **No Null**: `null`을 반환하거나 인수로 전달하지 말 것 (`Optional` 등 활용).

## 3. 포맷팅 (Formatting)
*   **Class**: `PascalCase`
*   **Method/Variable**: `camelCase`
*   **Constant**: `UPPER_SNAKE_CASE`
*   **Indent**: `4 Space`
