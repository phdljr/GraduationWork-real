package com.brother.graduationwork.service;

import com.brother.graduationwork.domain.*;
import com.brother.graduationwork.dto.OtherUserOrders;
import com.brother.graduationwork.dto.OrderDTO;
import com.brother.graduationwork.exception.CustomException;
import com.brother.graduationwork.exception.ErrorCode;
import com.brother.graduationwork.repository.OrderRepository;
import com.brother.graduationwork.repository.RoomRepository;
import com.brother.graduationwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;


@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl {

    private final UserService userService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WebSocketService webSocketService;

    public void addMenus(String userNickname, List<OrderDTO> orderList) {

        User findUser = userRepository.findUserWithJoinedRoom(userNickname);
        Room findRoom = findUser.getRoom();

        if (isNull(findRoom))
            throw new CustomException(ErrorCode.NOT_FOUND_ROOM);

        int beforeTotalAmount = findUser.getTotalAmount();
        findUser.resetTotalAmount();

        for (OrderDTO orderDTO : orderList) {
            Order order = new Order(orderDTO);
            order.setUser(findUser);
            orderRepository.save(order);
        }

        int afterTotalAmount = findUser.getTotalAmount();
        findRoom.addCurrAmount(beforeTotalAmount, afterTotalAmount);

        userRepository.save(findUser);
        roomRepository.save(findRoom);

        notifyUserOrders(userNickname, findRoom);
    }

    public void removeAllMenus(String userNickname) {
        User findUser = userRepository.findUserWithJoinedRoom(userNickname);
        findUser.getOrderList().clear();
        orderRepository.deleteAllByUserId(findUser.getId());
    }

    public void notifyUserOrders(String userNickname, Room findRoom) {

        Map<String, List<OrderDTO>> ordersChosenByPeopleInRoom = getOrdersChosenByPeopleInRoom(findRoom);

        OtherUserOrders otherUserOrders = OtherUserOrders.builder()
                .username(userNickname)
                .userMenus(ordersChosenByPeopleInRoom)
                .currAmount(findRoom.getCurrentAmount())
                .currNumOfPeople(findRoom.getCurrNumOfPeople())
                .userStatus(userService.getUserStatusInfo(findRoom.getUsers()))
                .build();

        webSocketService.notifyOtherUserMenus(findRoom.getId(), otherUserOrders);
    }

    // TODO: 최적의 Query 생각해보기
    public Map<String, List<OrderDTO>> getOrdersChosenByPeopleInRoom(Room findRoom) {

        Map<String, List<OrderDTO>> userMenus = new HashMap<>();
        
        // 방에 있는 사용자불러오기
        List<User> usersInRoom = userRepository.findAllByRoomId(findRoom.getId());
        for (User user : usersInRoom) {
            // 각 사용자의 메뉴 불러오기
            Long userId = user.getId();
            List<Order> userOrders = orderRepository.findAllByUserId(userId);

            List<OrderDTO> orderDTOS = new ArrayList<>();
            for (Order userOrder : userOrders) {
                OrderDTO orderDTO = new OrderDTO(userOrder.getMenuName(), userOrder.getPrice(), userOrder.getQuantity());
                orderDTOS.add(orderDTO);
            }

            userMenus.put(user.getNickname(), orderDTOS);
        }

        return userMenus;
    }
}
