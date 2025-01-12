import ws from 'k6/ws';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

// ========= 커스텀 메트릭 ========= //
const ws_connecting_time = new Trend('ws_connecting_time');
const ws_msgs_sent = new Counter('ws_msgs_sent');
const ws_msgs_received = new Counter('ws_msgs_received');
const ws_response_time = new Trend('ws_response_time');
const ws_errors = new Counter('ws_errors');

// ========= k6 옵션 ========= //
export const options = {
    scenarios: {
        websocket_test: {
            executor: 'shared-iterations',
            vus: parseInt(__ENV.VUS) || 1000, // 동시 사용자 수
            iterations: parseInt(__ENV.VUS) || 1000, // 총 실행 횟수
        },
    },
    thresholds: {
        ws_connecting_time: ['avg<1000'],
        ws_msgs_received: ['count >= 6'],
        ws_errors: ['count == 0'],
    },
};

// WebSocket 서버 URL
const url = __ENV.WS_URL || 'ws://localhost:8090/ws/chat';
const messagesPerUser =  300; // 사용자당 메시지 전송 수
const messageInterval = 500; // 메시지 간격(ms)

export default function () {
    const roomName = 'KRW-BTC'; // 고정된 룸 이름
    const userId = __VU; // VU(가상 사용자)의 ID를 userId로 사용
    const processedMessages = new Set(); // 중복 메시지 확인용 Set

    const res = ws.connect(
        url,
        {
            headers: {
                'X-Room-Name': roomName, // 룸 이름을 헤더로 전달
                'X-Claim-UserId': String(userId), // 사용자 ID를 헤더로 전달
            },
        },
        function (socket) {
            const startTime = Date.now(); // 연결 시작 시간

            socket.on('open', () => {
                const connectionTime = Date.now() - startTime;
                ws_connecting_time.add(connectionTime);
                // console.log(`[INFO] 연결 성공: 연결 시간 ${connectionTime}ms, userId=${userId}, roomName=${roomName}`);

                // 비동기로 메시지 전송
                async function sendMessages() {
                    for (let i = 0; i < messagesPerUser; i++) {
                        const message = JSON.stringify({
                            type: 'MESSAGE',
                            roomName: roomName,
                            sender: `user-${userId}`,
                            content: `Hello from K6! Message ${i + 1}`,
                        });

                        socket.send(message); // 메시지 전송
                        // ws_msgs_sent.add(1); // 송신 메트릭 증가
                        // console.log(`[INFO] 메시지 전송: ${message}`);

                        sleep(messageInterval / 1000); // 메시지 간격 대기 (ms → 초 변환)
                    }
                }

                sendMessages();
            });

            // 메시지 수신 처리
            socket.on('message', (msg) => {
                const receiveTime = Date.now() - startTime; // 수신 시간 계산
                try {
                    const parsed = JSON.parse(msg);
                    const messageKey = `${parsed.sender.name}-${parsed.content}-${parsed.timestamp}`;

                    if (processedMessages.has(messageKey)) {
                        console.log(`[DEBUG] 중복 메시지 무시: ${msg}`);
                        return;
                    }

                    processedMessages.add(messageKey);
                    // ws_msgs_received.add(1); // 수신 메트릭 증가
                    ws_response_time.add(receiveTime);

                    // console.log(`[INFO] 메시지 수신: ${msg}`);
                } catch (e) {
                    ws_errors.add(1);
                    console.error(`[ERROR] 메시지 파싱 실패: ${e.message}`);
                }
            });

            socket.on('error', (e) => {
                ws_errors.add(1);
                console.error(`[ERROR] WebSocket 오류 발생: ${e.message}`);
            });

            socket.on('close', () => {
                // console.log(`[INFO] WebSocket 연결 종료`);
            });

            // 연결 유지 시간 (10초 후 종료)
            socket.setTimeout(() => {
                // console.log(`[INFO] 테스트 종료`);
                socket.close();
            }, 100000);
        }
    );

    // WebSocket 연결 체크
    check(res, { 'WebSocket 연결 성공': (r) => r && r.status === 101 });
}
