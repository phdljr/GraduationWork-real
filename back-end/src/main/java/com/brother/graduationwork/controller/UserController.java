package com.brother.graduationwork.controller;

import com.brother.graduationwork.domain.Room;
import com.brother.graduationwork.domain.Status.Status;
import com.brother.graduationwork.domain.User;

import com.brother.graduationwork.dto.*;

import com.brother.graduationwork.exception.CustomException;
import com.brother.graduationwork.exception.ErrorCode;
import com.brother.graduationwork.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.brother.graduationwork.domain.Status.Status.*;
import static java.util.Objects.isNull;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/mypage")
    public MyPageDTO loadMyPage(@RequestBody OneParam oneParam) {
        String user_nickname = oneParam.getParam();
        User findUser = userService.findUserByNickName(user_nickname);
        if (isNull(findUser))
            throw new CustomException(ErrorCode.NOT_FOUND_USER);

        try {
            Room findUserRoom = findUser.getRoom();
            LoadRoomsDTO loadRoomDTO = new LoadRoomsDTO(
                    findUserRoom.getTitle(),
                    findUserRoom.getCurrNumOfPeople(),
                    findUserRoom.getMaximumPeople(),
                    findUserRoom.getCurrentAmount(),
                    findUserRoom.getMinimumOrderAmount(),
                    findUserRoom.getGatheringPlace(),
                    findUserRoom.getGatheringDetailPlace(),
                    findUserRoom.getRoomStatus()
            );
            MyPageDTO myPageDTO = new MyPageDTO(
                    loadRoomDTO, findUser.getMoney()
            );
            return myPageDTO;
        } catch (NullPointerException e) {
            return new MyPageDTO(
                    null, findUser.getMoney()
            );
        }
    }

    @PostMapping("/user")
    public Status addUser(@RequestBody UserRegisterDTO userDTO) {

        User newUser = new User(
                userDTO.getUser_email(), userDTO.getUser_pw(), userDTO.getUser_nickname(), userDTO.getUser_phoneNumber()
        );

        userService.registerUser(newUser);
        return Success;
    }

    @PostMapping("/login")
    public UserLoginReturnDTO Login(@RequestBody UserLoginDTO userLoginDTO) {

        String email = userLoginDTO.getUser_email();
        String pw = userLoginDTO.getUser_pw();

        return userService.loginUser(email, pw);
    }

    @PostMapping("/email")
    public Status checkDuplicatedEmail(@RequestBody OneParam oneParam) {
        return userService.duplicatedEmailCheck(oneParam.getParam());
    }

    @PostMapping("/nickname")
    public Status checkDuplicatedNickname(@RequestBody OneParam oneParam) {
        return userService.duplicatedNicknameCheck(oneParam.getParam());
    }
}
