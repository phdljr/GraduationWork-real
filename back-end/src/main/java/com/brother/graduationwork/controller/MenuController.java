package com.brother.graduationwork.controller;

import com.brother.graduationwork.dto.AddOrderDTO;
import com.brother.graduationwork.service.MenuServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuServiceImpl menuService;

    @PostMapping("/menu")
    public String addMenus(@RequestBody AddOrderDTO addOrderDTO) {

        menuService.removeAllMenus(addOrderDTO.getUser_nickname());
        menuService.addMenus(addOrderDTO.getUser_nickname(), addOrderDTO.getOrderList());

        return "Success";
    }
}
