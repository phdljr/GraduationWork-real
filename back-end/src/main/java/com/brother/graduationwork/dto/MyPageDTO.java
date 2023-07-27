package com.brother.graduationwork.dto;

import com.brother.graduationwork.domain.Status.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDTO {

    private LoadRoomsDTO loadRoomDTO;
    private int money;
}
