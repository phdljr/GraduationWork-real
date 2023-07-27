package com.brother.graduationwork.dto;

import lombok.Getter;

import java.util.List;


@Getter
public class AddOrderDTO {

    private String user_nickname;
    private List<OrderDTO> orderList;
}
