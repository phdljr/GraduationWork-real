package com.brother.graduationwork.controller;

import com.brother.graduationwork.dto.Message;
import com.brother.graduationwork.dto.OneParam;
import com.brother.graduationwork.service.RoomService;
import com.brother.graduationwork.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final WebSocketService webSocketService;
    private final RoomService roomService;

    @MessageMapping("/send/{roomId}")
    public void handleChatting(@Payload Message message, @DestinationVariable("roomId") Long roomId) {
        webSocketService.notifyOtherMessage(roomId, message);
    }

    @MessageMapping("/notify/{roomId}")
    public void handleStatus(@Payload OneParam oneParam, @DestinationVariable("roomId") Long roomId) {
        roomService.changeRoomStatus(oneParam.getParam(), roomId);
    }
}
