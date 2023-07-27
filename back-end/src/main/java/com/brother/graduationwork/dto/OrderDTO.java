package com.brother.graduationwork.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class OrderDTO {

    private String menuName;
    private int price;
    private int quantity;

    public OrderDTO(String menuName, int price, int quantity) {
        this.menuName = menuName;
        this.price = price;
        this.quantity = quantity;
    }
}
