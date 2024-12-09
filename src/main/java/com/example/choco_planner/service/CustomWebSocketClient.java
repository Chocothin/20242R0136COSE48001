package com.example.choco_planner.service;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomWebSocketClient extends WebSocketClient {

    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public CustomWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("WebSocket 연결 성공");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("메시지 수신: " + message);
        messageQueue.offer(message); // 메시지를 큐에 추가
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket 연결 종료: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket 오류: " + ex.getMessage());
    }

    public BlockingQueue<String> getMessageQueue() {
        return messageQueue;
    }
}
