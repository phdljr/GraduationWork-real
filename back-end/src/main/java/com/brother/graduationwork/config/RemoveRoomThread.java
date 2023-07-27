package com.brother.graduationwork.config;

import com.brother.graduationwork.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RemoveRoomThread implements Runnable {

    private final WebSocketService webSocketService;
    private Long roomId;

    @Autowired
    public RemoveRoomThread(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public void run() {
        webSocketService.notifyUserStatus("600/", roomId);
    }
}