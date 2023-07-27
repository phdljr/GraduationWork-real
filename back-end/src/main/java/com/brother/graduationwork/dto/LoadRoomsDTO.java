package com.brother.graduationwork.dto;

import com.brother.graduationwork.domain.Status.RoomStatus;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class LoadRoomsDTO {

    private String title;
    private int currNumOfPeople;
    private int maximumPeople;
    private int currAmount;
    private int minimumOrderAmount;
    private String gatheringPlace;
    private String gatheringDetailPlace;
    private RoomStatus roomStatus;
}
