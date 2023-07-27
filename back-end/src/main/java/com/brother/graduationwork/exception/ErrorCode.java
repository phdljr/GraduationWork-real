package com.brother.graduationwork.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_USER(NOT_FOUND, "그런 사용자가 없습니다."),
    NOT_FOUND_ROOM(NOT_FOUND, "그런 방이 없습니다"),
    FULL_OF_ROOM(LOCKED, "방이 가득 찼습니다."),
    ALREADY_HAS_ROOM(NOT_ACCEPTABLE, "이미 접속한 방이 있습니다."),
    ALREADY_EXIST_ROOM(NOT_ACCEPTABLE, "제목이 중복된 방이 있습니다."),
    SHORT_OF_MONEY(PAYMENT_REQUIRED, "돈이 부족합니다."),
    FAIL_TO_LOGIN(UNAUTHORIZED, "아이디 혹은 비밀번호가 틀립니다."),
    CANNOT_ACCESS_ROOM(FORBIDDEN, "모집 중이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
