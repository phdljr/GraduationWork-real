package com.brother.graduationwork.controller;

import com.brother.graduationwork.domain.Room;
import com.brother.graduationwork.dto.*;
import com.brother.graduationwork.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public String createRoom(@RequestBody CreateRoomDTO roomDTO) {
        roomService.createRoom(roomDTO);
        return "Success";
    }

    @GetMapping("/room/location/{location}/{count}")
    public List<LoadRoomsDTO> searchRoomByLocation(@PathVariable String location, @PathVariable int count) {
        List<Room> rooms = roomService.findByLocation(location, count);
        return getLoadRoomsDTOS(rooms);
    }

    @GetMapping("/room/title/{title}/{count}")
    public List<LoadRoomsDTO> searchRoomByTitle(@PathVariable String title, @PathVariable int count) {
        List<Room> rooms = roomService.findByTitle(title, count);
        return getLoadRoomsDTOS(rooms);
    }

    @GetMapping("/room/{limit}")
    public List<LoadRoomsDTO> getAllRoom(@PathVariable("limit") int limit) {
        log.info(String.valueOf(limit));
        List<Room> rooms = roomService.findAllRooms(limit);

        return getLoadRoomsDTOS(rooms);
    }

    private List<LoadRoomsDTO> getLoadRoomsDTOS(List<Room> rooms) {
        List<LoadRoomsDTO> loadRoomsDTOS = new ArrayList<>();
        rooms.forEach(r -> {
            LoadRoomsDTO loadRoomsDTO = new LoadRoomsDTO(
                    r.getTitle(),
                    r.getCurrNumOfPeople(),
                    r.getMaximumPeople(),
                    r.getCurrentAmount(),
                    r.getMinimumOrderAmount(),
                    r.getGatheringPlace(),
                    r.getGatheringDetailPlace(),
                    r.getRoomStatus());

            loadRoomsDTOS.add(loadRoomsDTO);
        });
        return loadRoomsDTOS;
    }

    @PostMapping("/room/join")
    public RoomDetailDTO joinRoom(@RequestBody JoinRoomDTO joinRoomDTO) {

        String user_nickname = joinRoomDTO.getUser_nickname();
        String roomTitle = joinRoomDTO.getRoomTitle();

        return roomService.userJoinRoom(user_nickname, roomTitle);
    }

    @PostMapping("/room/exit")
    public String exitRoom(@RequestBody OneParam param) {
        roomService.exitRoom(param.getParam());
        return "Success";
    }

    @PostMapping("/room/remove")
    public String removeRoom(@RequestBody OneParam param) {
        roomService.removeRoom(param.getParam());
        return "Success";
    }

    @PostMapping("/room/pay")
    public String payment(@RequestBody OneParam param) {
        roomService.payChargeForFood(param.getParam());
        return "Success";
    }
}
