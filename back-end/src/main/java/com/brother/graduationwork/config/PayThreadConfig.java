package com.brother.graduationwork.config;

import com.brother.graduationwork.domain.Room;
import com.brother.graduationwork.domain.User;
import com.brother.graduationwork.repository.RoomRepository;
import com.brother.graduationwork.repository.UserRepository;
import com.brother.graduationwork.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.brother.graduationwork.domain.Status.RoomStatus.ORDER;


@Component
public class PayThreadConfig implements Runnable {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private Long roomId;

    @Autowired
    public PayThreadConfig(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public void run() {
        Room room = roomRepository.findById(roomId).get();
        if (room.getFigure() != room.getCurrNumOfPeople()) {
            System.out.println("발생 함");
            List<User> users = room.getUsers();

            for (User user : users) {
                if (!user.getRoomStatus().equals(ORDER) && room.getCreatedBy().equals(user.getNickname())) {
                    int result = user.decreaseMoney(user.getTotalAmount());
                    user.setRoomStatus(ORDER);
                    room.addFigure();
                    userRepository.save(user);
                }
            }

            room.setRoomStatus(ORDER);
            roomRepository.save(room);
        } else {
            System.out.println("발생 안함");
        }
    }
}
