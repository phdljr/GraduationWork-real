package com.brother.graduationwork.service;

import com.brother.graduationwork.domain.Status.RoomStatus;
import com.brother.graduationwork.domain.Status.Status;
import com.brother.graduationwork.domain.User;
import com.brother.graduationwork.dto.UserLoginReturnDTO;
import com.brother.graduationwork.dto.UserStatusDTO;
import com.brother.graduationwork.exception.CustomException;
import com.brother.graduationwork.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.brother.graduationwork.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.brother.graduationwork.domain.Status.Status.Fail;
import static com.brother.graduationwork.domain.Status.Status.Success;
import static java.util.Objects.isNull;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void registerUser(User user) {
        userRepository.save(user);
    }

    @Override
    public UserLoginReturnDTO loginUser(String email, String pw) {

        User findUser = userRepository.findByEmailAndPw(email, pw);

        if (isNull(findUser))
            throw new CustomException(ErrorCode.FAIL_TO_LOGIN);

        return new UserLoginReturnDTO(
                findUser.getNickname(), findUser.getMoney()
        );
    }

    @Override
    public User findUserByNickName(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public int increaseMoney(String user_nickname, int chargedAmount) {
        User findUser = findUserByNickName(user_nickname);
        findUser.increaseMoney(chargedAmount);
        userRepository.save(findUser);

        return findUser.getMoney();
    }

    @Override
    public Status duplicatedEmailCheck(String email) {
        User user = userRepository.getByEmail(email);
        log.info("email user: " + user);
        if (isNull(user))
            return Success;

        return Fail;
    }

    @Override
    public Status duplicatedNicknameCheck(String nickname) {
        User user = userRepository.getByNickname(nickname);
        log.info("user: " + user);
        if (isNull(user))
            return Success;

        return Fail;
    }

    @Override
    public HashMap<String, RoomStatus> getUserStatusInfo(List<User> users) {

        HashMap<String, RoomStatus> userStatus = new HashMap<>();
        for (User user : users) {
            String user_nickname = user.getNickname();
            RoomStatus user_status = user.getRoomStatus();

            userStatus.put(user_nickname, user_status);
        }

        return userStatus;
    }
}
