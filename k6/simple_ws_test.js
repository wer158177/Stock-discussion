import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

// ========= 커스텀 메트릭 정의 ========= //
const ws_connecting_time = new Trend('ws_connecting_time'); // 연결 완료 시간(ms)을 측정하기 위한 커스텀 메트릭
const ws_sessions = new Counter('ws_sessions');             // 세션 연결 횟수를 세는 카운터
const ws_errors = new Counter('ws_errors');                 // 에러 발생 횟수를 세는 카운터

// ========= k6 옵션 ========= //
export const options = {
    scenarios: {
        session_test: {
            executor: 'constant-vus', // 일정한 사용자 수로 테스트
            vus: 1000,               // 동시 가상 사용자 수
            duration: '1m',          // 테스트 실행 시간
            exec: 'sessionTestScenario', // 실행할 함수 이름
        },
    },
    thresholds: {
        'ws_connecting_time': ['avg < 500'], // 평균 연결 시간이 500ms 미만이어야 함
        'ws_errors': ['count < 50'],         // 에러가 50건 미만이어야 함
    },
};

const DEBUG = __ENV.DEBUG || false; // 디버그 모드 활성화 여부 (환경 변수로 제어)

// ========= 공통 로직 ========= //
// WebSocket 이벤트 핸들러 설정 함수
function handleSocketEvents(socket, userId) {
    socket.on('open', () => {
        if (DEBUG) console.log(`[Session Test] VU ${userId} connected`); // 연결 성공 로그
        ws_sessions.add(1); // 세션 연결 카운터 증가
    });

    socket.on('close', () => {
        if (DEBUG) console.log(`[Session Test] VU ${userId} disconnected`); // 연결 종료 로그
        ws_sessions.add(-1); // 세션 종료 메트릭 감소
    });

    socket.on('error', (e) => {
        if (DEBUG) console.log(`[Session Test] VU ${userId} error: ${e}`); // 에러 로그
        ws_errors.add(1); // 에러 카운터 증가
    });
}

// WebSocket 연결 테스트 함수
function connectWebSocket(userId) {
    const url = __ENV.WS_URL || 'ws://localhost:8090/ws/test'; // WebSocket URL (환경 변수로 설정 가능)
    const startTime = Date.now();

    const res = ws.connect(url, {}, (socket) => {
        handleSocketEvents(socket, userId); // 이벤트 핸들러 설정
        socket.on('open', () => {
            const connectingTime = Date.now() - startTime; // 연결 완료 시간 계산
            ws_connecting_time.add(connectingTime); // 연결 시간 메트릭에 추가
            socket.close(); // 연결 후 바로 종료
        });
    });

    check(res, { 'status is 101': (r) => r && r.status === 101 }); // WebSocket 핸드셰이크 확인
}

// ========= 시나리오 ========= //
// 대량 연결 및 해제 테스트 시나리오
export function sessionTestScenario() {
    const userId = __VU; // 가상 사용자 ID
    connectWebSocket(userId); // WebSocket 연결 테스트 실행
    sleep(1); // 대량 테스트 안정성을 위해 대기
}
