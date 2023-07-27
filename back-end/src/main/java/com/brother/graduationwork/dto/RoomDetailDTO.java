package com.brother.graduationwork.dto;

import com.brother.graduationwork.domain.Status.RoomStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Builder
@ToString
public class RoomDetailDTO {

    private Long roomId;
    private String createdBy;
    private String title;
    private int currNumOfPeople;
    private int maximumPeople;
    private int currAmount;
    private int minimumOrderAmount;
    private String gatheringPlace;
    private String gatheringDetailPlace;
    private RoomStatus roomStatus;
    private Map<String, List<OrderDTO>> userMenus;
    private HashMap<String, RoomStatus> userStatus;
}
