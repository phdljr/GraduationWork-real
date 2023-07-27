package com.brother.graduationwork.controller;

import com.brother.graduationwork.domain.KakaoPay.KakaoPay;
import com.brother.graduationwork.dto.KakaoPayDTO;
import com.brother.graduationwork.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.Setter;


@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoPayController {

    private final UserService userService;

    private String user_nickname;
    private String chargingAmount;

    @Setter(onMethod_ = @Autowired)
    private KakaoPay kakaopay;

    @GetMapping("/kakaoPay")
    public void kakaoPayGet() {
        log.info("KakaoPay Get Request");
    }

    @PostMapping("/kakaoPay")
    @ResponseBody
    public String kakaoPay(@RequestBody KakaoPayDTO kakaoPayDTO) {

        log.info("KakaoPay Charging Request");

        user_nickname = kakaoPayDTO.getUser_nickname();
        chargingAmount = kakaoPayDTO.getChargingAmount();
        log.warn("user_nickname: " + user_nickname);
        String nextURL = kakaopay.kakaoPayReady(chargingAmount);
        log.info("KakaoPay nextURL is " + nextURL);

        return nextURL;
    }

    @PostMapping("/kakaoPaySuccess")
    @ResponseBody
    public int kakaoPaySuccess(@RequestBody String pg_token) {

        log.info("KakaoPayController.kakaoPaySuccess");

        pg_token = pg_token.replace("=", "");
        log.info("kakaoPaySuccess pg_token : " + pg_token);

        int chargedAmount = kakaopay.kakaoPayInfo(pg_token, chargingAmount).getAmount().getTotal();
        int userMoney = userService.increaseMoney(user_nickname, chargedAmount);

        return userMoney;
    }
}
