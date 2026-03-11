import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. 테스트 설정 (가상 유저 및 테스트 시간)
// 로컬 테스트용으로 설정 (본격적인 부하 땐 VUs와 target을 늘리면 됩니다)
export const options = {
    // 1단계: 10초 동안 가상 유저(VU) 100명으로 웜업
    // 2단계: 30초 동안 2000명의 VU 유지
    // 3단계: 10초 동안 점점 0명으로 종료
    stages: [
        { duration: '10s', target: 100 },
        { duration: '30s', target: 2000 },
        { duration: '10s', target: 0 },
    ],
};

// 백엔드 API 서버 주소 
// Docker Desktop 실행 시 host.docker.internal 사용, 로컬 환경 변수로 재정의 가능
const BASE_URL = __ENV.API_URL || 'http://host.docker.internal:18080';
const CONCERT_ID = 1;

export default function () {
    // --- 1. 대기열 진입 및 토큰 발급 (POST /queue/tokens) ---
    const tokenPayload = JSON.stringify({ concertId: CONCERT_ID });
    const tokenHeaders = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const tokenRes = http.post(`${BASE_URL}/queue/tokens`, tokenPayload, tokenHeaders);

    // 응답 검증 (Http Status 200, JWT token 존재 여부)
    check(tokenRes, {
        '토큰 발급 성공 (Status 200)': (r) => r.status === 200,
        '응답에 token 포함': (r) => r.json() && r.json('token') !== undefined,
    });

    if (tokenRes.status !== 200) {
        // 토큰 발급 실패 시나리오 종료
        return;
    }

    const responseBody = tokenRes.json();
    const token = responseBody.token;
    let status = responseBody.status;

    // --- 2. 대기 상태 확인 및 폴링 (GET /queue/status) ---
    // 처음 발급 받았을 때 상태가 WAITING 이면 순서가 될 때까지 폴링
    while (status === 'WAITING') {
        const statusHeaders = {
            headers: {
                'Content-Type': 'application/json',
                'Queue-Token': token,
            },
        };

        const statusRes = http.get(`${BASE_URL}/queue/status?concertId=${CONCERT_ID}`, statusHeaders);

        check(statusRes, {
            '폴링 응답 성공 (Status 200)': (r) => r.status === 200,
        });

        if (statusRes.status === 200) {
            status = statusRes.json('status');
        } else {
            // 서버 에러 등이 발생하면 폴링 루프 탈출
            break; 
        }

        // 실 서비스 환경 시뮬레이션을 위해 3초 간격으로 대기 후 재요청
        // (사용자가 화면에서 남은 순서를 보고 기다리는 시간)
        sleep(3);
    }

    // --- 3. 통과 확인 ---
    check(status, {
        '대기열 최종 통과 (ACTIVE 상태)': (s) => s === 'ACTIVE',
    });
    
    // 이 뒤에 예매/결제 등의 시나리오를 이어 붙일 수 있습니다.
}
