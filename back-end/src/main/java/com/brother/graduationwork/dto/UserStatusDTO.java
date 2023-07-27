package com.brother.graduationwork.dto;

import com.brother.graduationwork.domain.Status.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {

    private String user_nickname;
    private RoomStatus roomStatus;
}
