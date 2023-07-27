package com.brother.graduationwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomDTO {

    private String title;

    private String createdBy;

    private String gatheringPlace;

    private String gatheringDetailPlace;

    private int minimumOrderAmount;

    private int numOfPeople;
}
