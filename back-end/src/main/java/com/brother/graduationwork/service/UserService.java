package com.brother.graduationwork.service;

import com.brother.graduationwork.domain.Status.RoomStatus;
import com.brother.graduationwork.domain.Status.Status;
import com.brother.graduationwork.domain.User;
import com.brother.graduationwork.dto.UserLoginReturnDTO;
import com.brother.graduationwork.dto.UserStatusDTO;

import java.util.HashMap;
import java.util.List;


public interface UserService {

    void registerUser(User user);

    User findUserByNickName(String nickname);

    int increaseMoney(String user_nickname, int chargedAmount);

    UserLoginReturnDTO loginUser(String email, String pw);

    Status duplicatedEmailCheck(String email);

    Status duplicatedNicknameCheck(String nickname);

    HashMap<String, RoomStatus> getUserStatusInfo(List<User> users);
}
