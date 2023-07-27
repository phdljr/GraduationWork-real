package com.brother.graduationwork.service;

import com.brother.graduationwork.config.PayThreadConfig;
import com.brother.graduationwork.config.RemoveRoomThread;
import com.brother.graduationwork.config.ThreadConfig;
import com.brother.graduationwork.domain.*;
import com.brother.graduationwork.dto.CreateRoomDTO;
import com.brother.graduationwork.dto.Message;
import com.brother.graduationwork.dto.RoomDetailDTO;
import com.brother.graduationwork.exception.CustomException;
import com.brother.graduationwork.exception.ErrorCode;
import com.brother.graduationwork.repository.OrderRepository;
import com.brother.graduationwork.repository.RoomRepository;
import com.brother.graduationwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.brother.graduationwork.domain.Status.RoomStatus.*;
import static com.brother.graduationwork.exception.ErrorCode.*;
import static java.util.Objects.isNull;


@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final UserService userService;
    private final MenuServiceImpl menuService;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final OrderRepository orderRepository;
    private final WebSocketService webSocketService;

    private final ThreadConfig threadConfig;
    private final ThreadPoolTaskScheduler scheduler;

    @Override
    public List<Room> findByTitle(String title, int limit) {

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        Page<Room> rooms = roomRepository.findAllByTitleLike(title, pageable);
        return rooms.getContent();
    }

    @Override
    public List<Room> findByLocation(String location, int limit) {

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        Page<Room> rooms = roomRepository.findAllByGatheringPlaceLike(location, pageable);
        return rooms.getContent();
    }

    @Override
    public List<Room> findAllRooms(int limit) {

        return roomRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();
    }

    @Override
    public void createRoom(CreateRoomDTO roomDTO) {

        User findUser = userRepository.findByNickname(roomDTO.getCreatedBy());

        if (isNull(findUser))
            throw new CustomException(NOT_FOUND_USER);
        else if (findUser.isJoinRoom())
            throw new CustomException(ALREADY_HAS_ROOM);

        String roomTitle = roomDTO.getTitle();
        Room findRoom = roomRepository.findByTitle(roomTitle);
        if (!isNull(findRoom))
            throw new CustomException(ALREADY_EXIST_ROOM);

        Room room = Room.builder()
                .title(roomDTO.getTitle())
                .gatheringPlace(roomDTO.getGatheringPlace())
                .gatheringDetailPlace(roomDTO.getGatheringDetailPlace())
                .createdBy(roomDTO.getCreatedBy())
                .minimumOrderAmount(roomDTO.getMinimumOrderAmount())
                .numOfPeople(roomDTO.getNumOfPeople())
                .build();
        room.addPerson(findUser);
        room.addFigure();
        findUser.setRoomStatus(READY);
        // 생성한 방 저장
        roomRepository.save(room);

        // 이미 참가한 방이 있다고 상태 변경
        findUser.setJoinRoom(true);
    }

    @Override
    public RoomDetailDTO userJoinRoom(String user_nickname, String roomTitle) {

        Room findRoom = roomRepository.findByTitle(roomTitle);
        User findUser = userRepository.findByNickname(user_nickname);
        boolean joinRoom = false;

        if (isNull(findRoom)) {
            log.warn("방이 존재하지 않음");
            throw new CustomException(NOT_FOUND_ROOM);
        } else if (isNull(findUser)) {
            log.warn("유저가 존재하지 않음");
            throw new CustomException(NOT_FOUND_USER);
        } else if (!findRoom.getRoomStatus().equals(RECRUIT)) {
            Room userRoom = findUser.getRoom();
            if (isNull(userRoom) || !findUser.getRoom().equals(findRoom)) {
                log.warn("접근할 수 없습니다.");
                throw new CustomException(CANNOT_ACCESS_ROOM);
            }
        } else if (findUser.isJoinRoom()) {
            if (!findUser.getRoom().equals(findRoom)) {
                log.warn("유저가 현재 접속 중인 방이 있음");
                throw new CustomException(ALREADY_HAS_ROOM);
            } else {
                joinRoom = true;
            }
        } else if (findRoom.getCurrNumOfPeople() == findRoom.getMaximumPeople()) {
            log.warn("방이 가득찼음");
            throw new CustomException(FULL_OF_ROOM);
        }

        if (!joinRoom) {
            findRoom.addPerson(findUser);
            findUser.setJoinRoom(true);
        }

        RoomDetailDTO roomDetailInfo = getRoomDetailInfo(findRoom);
        menuService.notifyUserOrders(findUser.getNickname(), findRoom);

        return roomDetailInfo;
    }

    public RoomDetailDTO getRoomDetailInfo(Room findRoom) {

        RoomDetailDTO roomDetailDTO = RoomDetailDTO.builder()
                .roomId(findRoom.getId())
                .createdBy(findRoom.getCreatedBy())
                .title(findRoom.getTitle())
                .currNumOfPeople(findRoom.getCurrNumOfPeople())
                .maximumPeople(findRoom.getMaximumPeople())
                .currAmount(findRoom.getCurrentAmount())
                .minimumOrderAmount(findRoom.getMinimumOrderAmount())
                .gatheringPlace(findRoom.getGatheringPlace())
                .gatheringDetailPlace(findRoom.getGatheringDetailPlace())
                .userMenus(menuService.getOrdersChosenByPeopleInRoom(findRoom))
                .roomStatus(findRoom.getRoomStatus())
                .userStatus(userService.getUserStatusInfo(findRoom.getUsers()))
                .build();

        return roomDetailDTO;
    }

    @Override
    public void exitRoom(String username) {

        User findUser = userRepository.findUserWithJoinedRoom(username);
        Room findRoom = findUser.getRoom();

        if (isNull(findRoom))
            throw new CustomException(NOT_FOUND_ROOM);

        int totalAmount = findUser.getTotalAmount();

        findUser.exitRoom();
        findRoom.deletePerson(findUser);
        findRoom.addCurrAmount(totalAmount, 0);
        orderRepository.deleteAllByUserId(findUser.getId());

        menuService.notifyUserOrders(findUser.getNickname(), findRoom);
    }

    @Override
    public void removeRoom(String username) {

        User findUser = userRepository.findUserWithJoinedRoom(username);

        Room findRoom = findUser.getRoom();
        List<User> users = findRoom.getUsers();

        for (User user : users) {
            user.exitRoom();
            orderRepository.deleteAllByUserId(user.getId());
        }

        findRoom.getUsers().clear();
        roomRepository.delete(findRoom);

        webSocketService.notifyUserStatus("600/", findRoom.getId());
    }

    @Override
    public void payChargeForFood(String username) {

        User findUser = userRepository.findUserWithJoinedRoom(username);

        if (isNull(findUser))
            throw new CustomException(NOT_FOUND_USER);

        String nickname = findUser.getNickname();
        Room findUserRoom = findUser.getRoom();
        String createdBy = findUserRoom.getCreatedBy();
        Long roomId = findUserRoom.getId();

        if (nickname.equals(createdBy)) {
            webSocketService.notifyPayment(roomId, "결제를 부탁드립니다.");
        } else {
            int result = findUser.decreaseMoney(findUser.getTotalAmount());
            if (result == 1) {      // 결제에 성공
                webSocketService.notifyUserStatus("202/" + username, roomId);
                findUser.setRoomStatus(ORDER);
                findUserRoom.addFigure();
//                findUser.resetTotalAmount();
                if (findUserRoom.getFigure() == findUserRoom.getCurrNumOfPeople()) {
                    webSocketService.notifyUserStatus("203/", roomId);
                }
            } else {                // 결제에 실패
                log.warn("결제에 실패했습니다.");
                throw new CustomException(ErrorCode.SHORT_OF_MONEY);
            }
        }
    }

    @Override
    public void changeRoomStatus(String param, Long roomId) {

        String[] split = param.split("/");
        String status = split[0];
        String value = split[1];

        // 코드가 201일 때
        if (status.equals("201")) {

            int deliveryFee = Integer.parseInt(value);
            Optional<Room> findRoom = roomRepository.findById(roomId);
            if (findRoom.isPresent()) {
                Room room = findRoom.get();             // 방 불러오기
                room.setRoomStatus(READY);              // 방 상태 변경

                List<User> users = room.getUsers();     // 각 사용자마다 지불해야 하는 돈에 배달비를 더함
                for (User user : users)
                    user.addTotalAmount(deliveryFee);

                room.resetFigure();                     // Figure 초기화
                room.addFigure();
                room.addCurrAmount(0, users.size() * deliveryFee);

                Message message = new Message("", String.valueOf(room.getCurrentAmount()));
                webSocketService.notifyOtherMessage(roomId, message);

//                PayThreadConfig payThread = threadConfig.getPayThread();
//                payThread.setRoomId(roomId);
//                scheduler.schedule(
//                        payThread,
//                        new Date(System.currentTimeMillis() + 5000)
//                );
            } else {
                webSocketService.notifyUserStatus("500/", roomId);
            }

            webSocketService.notifyUserStatus("201/", roomId);
            return;
        }

        // 코드가 201이 아닐 때
        User findUser = userRepository.findUserWithJoinedRoom(value);
        if (isNull(findUser)) {
            webSocketService.notifyUserStatus("500/", roomId);
        }
        Room findRoom = findUser.getRoom();

        switch (status) {
            case "100":
                if (findUser.getRoomStatus().equals(RECRUIT)) {
                    findRoom.addFigure();
                    findUser.setRoomStatus(READY);
                    webSocketService.notifyUserStatus("100/" + findUser.getNickname(), roomId);
                } else
                    webSocketService.notifyUserStatus("500/", roomId);

                if (findRoom.getFigure() == findRoom.getCurrNumOfPeople()) {
                    webSocketService.notifyUserStatus("200/", roomId);
                }

                break;
            case "101":
                findUser.setRoomStatus(RECRUIT);
                findRoom.subtractFigure();
                webSocketService.notifyUserStatus("101/" + findUser.getNickname(), roomId);
                break;
            case "300":
                findUser.setRoomStatus(ORDER);
                findRoom.setRoomStatus(ORDER);
                webSocketService.notifyUserStatus("300/" + findUser.getNickname(), roomId);
                findRoom.resetFigure();
                findRoom.addFigure();
                break;
            case "400":
                findUser.setRoomStatus(DISTRIBUTE); // 방장
                findRoom.setRoomStatus(DISTRIBUTE);
                findRoom.resetFigure();              // Figure 초기화
                findRoom.addFigure();
                webSocketService.notifyUserStatus("400/" + findUser.getNickname(), roomId);
                break;
            case "401":
                findUser.setRoomStatus(DISTRIBUTE); // 사용자
                findRoom.addFigure();
                webSocketService.notifyUserStatus("401/" + findUser.getNickname(), roomId);
                if (findRoom.getFigure() == findRoom.getCurrNumOfPeople()) {
                    webSocketService.notifyUserStatus("402/", roomId);

                    // 3초 뒤에 터짐
                    RemoveRoomThread removeRoomThread = threadConfig.getRemoveRoomThread();
                    removeRoomThread.setRoomId(roomId);
                    scheduler.schedule(
                            removeRoomThread,
                            new Date(System.currentTimeMillis() + 3000)
                    );

                    String createdBy = findRoom.getCreatedBy();
                    User createdUser = userRepository.findByNickname(createdBy);
                    createdUser.increaseMoney(findRoom.getCurrentAmount() - createdUser.getTotalAmount());
                    removeRoom(findRoom.getCreatedBy());
                }
                break;
            case "700":
                findUser.increaseMoney(findUser.getTotalAmount());
                findUser.resetTotalAmount();
                webSocketService.notifyUserStatus("700/" + findUser.getNickname(), roomId);
                break;
            default:
                webSocketService.notifyUserStatus("500/", roomId);
        }
    }
}
