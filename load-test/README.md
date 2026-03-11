# 부하 테스트 (Load Testing)

프로젝트 Phase 6 부하 테스트를 위한 k6 스크립트 모음입니다.

## 사전 준비
테스트를 실행하기 전에 백엔드 (API 서버 및 Redis)가 실행 중이어야 합니다.
로컬 테스트의 경우 Docker Desktop을 활용하여 k6를 실행하는 것을 권장합니다.

## 테스트 실행 방법

터미널에서 이 디렉토리(`load-test`)로 이동한 후 아래 명령어를 실행하세요.

```bash
# Linux / macOS
docker run --rm -v $(pwd):/src -w /src grafana/k6 run queue-entry.js
```

### 윈도우 Git Bash 환경인 경우 (경로 변환 에러 해결)
Git Bash에서는 `/src` 같은 경로를 자동으로 윈도우 경로(`C:/Program Files/Git/src` 등)로 변환해버리는 문제가 있습니다. 이를 방지하려면 경로 이름 앞에 `//`를 붙이거나 환경 변수를 설정해야 합니다.
```bash
MSYS_NO_PATHCONV=1 docker run --rm -v $(pwd):/src -w /src grafana/k6 run queue-entry.js
```
또는
```bash
docker run --rm -v "/$(pwd):/src" -w //src grafana/k6 run queue-entry.js
```

### 윈도우(CMD) 환경인 경우
```cmd
docker run --rm -v %cd%:/src -w /src grafana/k6 run queue-entry.js
```

### 윈도우(PowerShell) 환경인 경우
```powershell
docker run --rm -v ${PWD}:/src -w /src grafana/k6 run queue-entry.js
```

## 스크립트 설명
- `queue-entry.js`: 대기열 토큰 발급 후, 내 차례가 올 때까지 3초마다 폴링(GET `/queue/status`)하며 `ACTIVE` 상태가 될 때까지 기다리는 시나리오를 시뮬레이션합니다. 

## 환경 변수
API 서버 주소가 다를 경우 환경 변수로 덮어씌울 수 있습니다. (기본값: `http://host.docker.internal:18080`)
```bash
docker run --rm -e API_URL="http://your-server-ip:8080" -v $(pwd):/src -w /src grafana/k6 run queue-entry.js
```
