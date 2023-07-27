package com.brother.graduationwork.service;

import com.brother.graduationwork.dto.Message;
import com.brother.graduationwork.dto.OtherUserOrders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyOtherUserMenus(Long roomId, OtherUserOrders otherUserOrders) {
        messagingTemplate.convertAndSend("/room/" + roomId, otherUserOrders);
    }

    public void notifyOtherMessage(Long roomId, Message message) {
        messagingTemplate.convertAndSend("/chat/receive/" + roomId, message);
    }

    public void notifyPayment(Long roomId, String message) {
        messagingTemplate.convertAndSend("/room/" + roomId, message);
    }

    public void notifyUserStatus(String message, Long roomId) {
        messagingTemplate.convertAndSend("/status/notify/" + roomId, message);
    }
}
