package com.brother.graduationwork.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class UserLoginReturnDTO {

    private final String nickname;
    private final int cash;
}
