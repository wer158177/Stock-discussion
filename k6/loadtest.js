import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';
import { randomItem, randomString } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

// ========= 커스텀 메트릭 정의 ========= //
const ws_connecting_time = new Trend('ws_connecting_time'); // 연결 완료 시간(ms)을 측정하기 위한 커스텀 메트릭
const ws_msgs_sent = new Counter('ws_msgs_sent');           // 전송된 메시지 수를 세는 카운터
const ws_msgs_received = new Counter('ws_msgs_received');   // 수신된 메시지 수를 세는 카운터
const ws_reconnects = new Counter('ws_reconnects');         // 재연결 횟수를 세는 카운터
const ws_errors = new Counter('ws_errors');                 // 에러 발생 횟수를 세는 카운터

// ========= k6 옵션 ========= //
export const options = {
    scenarios: {
        chat_active_users: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 1000 },  // 1분 동안 500명으로 증가
                { duration: '2m', target: 3000 }, // 2분 동안 1,000명으로 증가
                { duration: '1m', target: 0 },    // 1분 동안 0명으로 감소
            ],
            exec: 'activeChatScenario',
        },
        chat_lurkers: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 500 },  // 1분 동안 200명으로 증가
                { duration: '2m', target: 1500 },  // 3분 동안 200명 유지
                { duration: '1m', target: 0 },    // 1분 동안 0명으로 감소
            ],
            exec: 'lurkerScenario',
        },
        chat_switchers: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 50 },   // 1분 동안 50명으로 증가
                { duration: '2m', target: 100 },   // 3분 동안 50명 유지
                { duration: '3m', target: 100 },   // 3분 동안 50명 유지
                { duration: '1m', target: 0 },    // 1분 동안 0명으로 감소
            ],
            exec: 'switcherScenario',
        },
    },
    thresholds: {
        'ws_connecting_time': ['avg < 500'],     // 평균 연결 시간 500ms 미만
        'ws_msgs_sent': ['count > 10000'],        // 총 메시지 전송 수 5,000건 이상
        'ws_msgs_received': ['count > 90000'],    // 총 메시지 수신 수 7,000건 이상
        'ws_reconnects': ['count < 100'],        // 재연결 횟수 100건 미만
        'ws_errors': ['count < 50'],             // 에러 50건 미만
    },
};

const roomList = ['KRW-BTC', 'KRW-ETH', 'KRW-XRP']; // 사용 가능한 방 목록
const DEBUG = __ENV.DEBUG || false; // 디버그 모드 활성화 여부 (환경 변수로 제어)

// ========= 공통 로직 ========= //
// WebSocket 이벤트 핸들러 설정 함수
function handleSocketEvents(socket, userId, roomName, role) {
    socket.on('open', () => {
        if (DEBUG) console.log(`[${role}] VU ${userId} joined room=${roomName}`); // 연결 성공 로그
    });

    socket.on('message', (msg) => {
        ws_msgs_received.add(1); // 수신 메시지 카운터 증가
    });

    socket.on('close', () => {
        if (DEBUG) console.log(`[${role}] VU ${userId} closed`); // 연결 종료 로그
    });

    socket.on('error', (e) => {
        if (DEBUG) console.log(`[${role}] VU ${userId} error: ${e}`); // 에러 로그
        ws_errors.add(1); // 에러 카운터 증가
    });
}

// WebSocket 연결 설정 함수
function connectWebSocket(userId, roomName, role, onOpenCallback) {
    const url = __ENV.WS_URL || 'ws://localhost:8090/ws/chat'; // WebSocket URL (환경 변수로 설정 가능)
    const params = {
        headers: {
            'X-Claim-userId': String(userId), // 사용자 ID를 헤더에 추가
            'X-Room-Name': roomName,          // 방 이름을 헤더에 추가
        },
    };
    const startTime = Date.now();

    const res = ws.connect(url, params, (socket) => {
        handleSocketEvents(socket, userId, roomName, role); // 이벤트 핸들러 설정
        socket.on('open', () => {
            const connectingTime = Date.now() - startTime; // 연결 완료 시간 계산
            ws_connecting_time.add(connectingTime); // 연결 시간 메트릭에 추가
            onOpenCallback(socket); // 추가 로직 실행
        });
    });

    check(res, { 'status is 101': (r) => r && r.status === 101 }); // WebSocket 핸드셰이크 확인
}

// 메시지 전송 함수 (비동기)
async function sendMessages(socket, userId, roomName, messageCount, delay, prefix) {
    for (let i = 0; i < messageCount; i++) {
        const msg = {
            type: 'MESSAGE',
            content: `${prefix} #${userId}: ${randomString(10)}`, // 랜덤 메시지 내용
            roomName,
            sender: `${prefix.toLowerCase()}User${userId}`, // 발신자 정보
        };
        socket.send(JSON.stringify(msg)); // 메시지 전송
        ws_msgs_sent.add(1); // 전송 메시지 카운터 증가
        await new Promise((resolve) => setTimeout(resolve, delay)); // 지연 시간 적용
    }
    socket.close(); // 메시지 전송 완료 후 연결 종료
}

// ========= 시나리오 ========= //
// 적극적으로 메시지를 전송하는 사용자 시나리오
export function activeChatScenario() {
    const userId = __VU; // 가상 사용자 ID
    const roomName = randomItem(roomList); // 랜덤 방 선택

    connectWebSocket(userId, roomName, 'Active', (socket) => {
        sendMessages(socket, userId, roomName, 1000, 1000, 'Active'); // 10개의 메시지를 1초 간격으로 전송
    });
    sleep(1);
}

// 메시지를 거의 보내지 않는 사용자 시나리오
export function lurkerScenario() {
    const userId = __VU; // 가상 사용자 ID
    const roomName = randomItem(roomList); // 랜덤 방 선택

    connectWebSocket(userId, roomName, 'Lurker', (socket) => {
        const msg = {
            type: 'HELLO',
            content: `Lurker #${userId}`, // 간단한 환영 메시지
            roomName,
            sender: `lurkerUser${userId}`,
        };
        socket.send(JSON.stringify(msg)); // 메시지 1회 전송
        ws_msgs_sent.add(1); // 전송 메시지 카운터 증가
    });
    sleep(1);
}

// 방을 변경하거나 재연결하는 사용자 시나리오
export function switcherScenario() {
    const userId = __VU; // 가상 사용자 ID
    let currentRoom = randomItem(roomList); // 랜덤 방 선택

    connectWebSocket(userId, currentRoom, 'Switcher', (socket) => {
        sendMessages(socket, userId, currentRoom, 3, 500, 'Switcher'); // 3개의 메시지를 0.5초 간격으로 전송
    });

    sleep(3);

    const newRoom = randomItem(roomList.filter((r) => r !== currentRoom)); // 다른 방 선택
    ws_reconnects.add(1); // 재연결 카운터 증가

    connectWebSocket(userId, newRoom, 'Switcher', (socket) => {
        sendMessages(socket, userId, newRoom, 3, 500, 'Switcher'); // 새 방에서 메시지 전송
    });
    sleep(3);
}
