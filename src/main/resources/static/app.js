let stompClient = null;
let audioContext;
let processor;
let source;
let audioStream;
let audioChunks = []; // 오디오 데이터를 저장할 배열
const chunkThreshold = 16; // 전송 전에 모아야 할 청크의 개수

// Base64 인코딩 함수 (Uint8Array를 지원)
function arrayBufferToBase64(buffer) {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
}

// WebSocket 연결 상태 설정
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

// WebSocket 연결
function connect() {
    var socket = new SockJS('https://monthly-madge-choco-planner-59fb550a.koyeb.app/gs-guide-websocket');
    console.log("WebSocket 연결 시도 중...");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/translated', function (message) {
            const translatedMessage = message.body;
            $('#translatedMessages').append(`<p>${translatedMessage}</p>`);
        });
    }, function (error) {
        console.error('WebSocket 연결 오류: ', error);
        alert('WebSocket 연결에 실패했습니다.');
    });
}

// WebSocket 연결 해제
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect(function () {
            setConnected(false);
            console.log("Disconnected");
        });
    } else {
        setConnected(false);
        console.log("Disconnected");
    }
}

// 오디오 데이터를 청크 단위로 전송하는 함수
function sendAudioDataChunk(audioData, classId, userId) {
    // 청크를 배열에 추가
    audioChunks.push(audioData);

    // 청크가 일정 개수 이상 모였을 때 서버로 전송
    if (audioChunks.length >= chunkThreshold) {
        const combinedAudioData = audioChunks.join(''); // 모든 청크를 하나로 합침
        audioChunks = []; // 배열 초기화

        if (stompClient && stompClient.connected) {
            stompClient.send("/app/voice", {}, JSON.stringify({
                audioData: combinedAudioData,
                classId: classId,
                userId: userId
            }));
        } else {
            console.warn('WebSocket이 연결되지 않아 오디오 데이터를 보낼 수 없습니다.');
        }
    }
}

// 녹음 시작
function startRecording() {
    // 수업 ID 가져오기
    const classId = $("#classId").val();
    const userId = $("#userId").val();

    if (!classId || !userId) {
        alert("수업 ID와 사용자 ID를 입력해주세요.");
        return;
    }

    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(function (stream) {
            audioContext = new (window.AudioContext || window.webkitAudioContext)();
            source = audioContext.createMediaStreamSource(stream);

            processor = audioContext.createScriptProcessor(4096, 1, 1);

            source.connect(processor);
            processor.connect(audioContext.destination);

            processor.onaudioprocess = async function (e) {
                const inputData = e.inputBuffer.getChannelData(0);
                const originalSampleRate = audioContext.sampleRate;
                const targetSampleRate = 16000;

                try {
                    let resampledData = await resampleBuffer(inputData, originalSampleRate, targetSampleRate);

                    let pcmData = new Int16Array(resampledData.length);
                    for (let i = 0; i < resampledData.length; i++) {
                        let s = Math.max(-1, Math.min(1, resampledData[i]));
                        pcmData[i] = s < 0 ? s * 0x8000 : s * 0x7FFF;
                    }

                    let uint8Array = new Uint8Array(pcmData.buffer);

                    // Base64 인코딩
                    let base64data = arrayBufferToBase64(uint8Array);

                    // 모은 청크를 서버로 전송
                    sendAudioDataChunk(base64data, classId, userId);
                } catch (err) {
                    console.error('리샘플링 오류:', err);
                }
            };

            audioStream = stream;
            console.log("녹음 시작됨");
            $("#startRecording").prop("disabled", true);
            $("#stopRecording").prop("disabled", false);
        })
        .catch(function (err) {
            console.error('마이크 접근 오류:', err);
            alert('마이크 접근에 실패했습니다: ' + err.message);
        });
}

// Resample 함수: 원본 샘플 레이트에서 타겟 샘플 레이트로 변환
function resampleBuffer(buffer, originalSampleRate, targetSampleRate) {
    if (originalSampleRate === targetSampleRate) {
        return buffer;
    }
    const sampleRateRatio = originalSampleRate / targetSampleRate;
    const newLength = Math.round(buffer.length / sampleRateRatio);
    const result = new Float32Array(newLength);
    let offsetResult = 0;
    let offsetBuffer = 0;
    while (offsetResult < result.length) {
        const nextOffsetBuffer = Math.round((offsetResult + 1) * sampleRateRatio);
        let accum = 0, count = 0;
        for (let i = offsetBuffer; i < nextOffsetBuffer && i < buffer.length; i++) {
            accum += buffer[i];
            count++;
        }
        result[offsetResult] = accum / count;
        offsetResult++;
        offsetBuffer = nextOffsetBuffer;
    }
    return result;
}

// 녹음 중지
function stopRecording() {
    if (processor) {
        processor.disconnect();
        processor = null;
    }
    if (source) {
        source.disconnect();
        source = null;
    }
    if (audioContext) {
        audioContext.close();
        audioContext = null;
    }
    if (audioStream) {
        audioStream.getTracks().forEach(track => track.stop());
    }

    console.log("녹음 중지됨");

    // UI 업데이트
    $("#startRecording").prop("disabled", false);
    $("#stopRecording").prop("disabled", true);
}

$(document).ready(function () {
    // 폼 제출 기본 동작 방지
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    // 버튼 클릭 이벤트 핸들러 설정
    $("#connect").click(function () { connect(); });
    $("#disconnect").click(function () { disconnect(); });

    $("#startRecording").click(function () {
        startRecording();
    });

    $("#stopRecording").click(function () {
        stopRecording();
    });

    // 초기 UI 상태 설정
    setConnected(false);
    $("#conversation").hide();
    $("#stopRecording").prop("disabled", true);
});
