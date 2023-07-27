package com.brother.graduationwork.service;

import com.brother.graduationwork.domain.Room;
import com.brother.graduationwork.dto.CreateRoomDTO;
import com.brother.graduationwork.dto.RoomDetailDTO;

import java.util.List;


public interface RoomService {

    void createRoom(CreateRoomDTO roomDTO);

    List<Room> findAllRooms(int limit);

    RoomDetailDTO userJoinRoom(String username, String roomTitle);

    void exitRoom(String username);

    void removeRoom(String username);

    void payChargeForFood(String username);

    void changeRoomStatus(String param, Long roomId);

    List<Room> findByTitle(String title, int limit);

    List<Room> findByLocation(String location, int limit);
}
