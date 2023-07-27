package com.brother.graduationwork.dto;

import com.brother.graduationwork.domain.Status.RoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Builder
public class OtherUserOrders {

    private String username;
    private int currAmount;
    private Map<String, List<OrderDTO>> userMenus;
    private HashMap<String, RoomStatus> userStatus;
    private int currNumOfPeople;
}
