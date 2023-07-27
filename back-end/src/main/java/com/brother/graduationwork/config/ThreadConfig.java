package com.brother.graduationwork.config;

import com.brother.graduationwork.repository.RoomRepository;
import com.brother.graduationwork.repository.UserRepository;
import com.brother.graduationwork.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


@Configuration
@RequiredArgsConstructor
public class ThreadConfig {

    private final WebSocketService webSocketService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Bean
    public ThreadPoolTaskScheduler getScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        return scheduler;
    }

    @Bean
    public RemoveRoomThread getRemoveRoomThread() {
        return new RemoveRoomThread(webSocketService);
    }

    @Bean
    public PayThreadConfig getPayThread() {
        return new PayThreadConfig(roomRepository, userRepository);
    }
}
