package com.brother.graduationwork.controller;

import com.brother.graduationwork.dto.OneParam;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Random;

@RestController
@Slf4j
public class CoolsmsController {

    @PostMapping("/sendSMS")
    public String sendSMS(@RequestBody OneParam oneParamDTO) {

        String phoneNumber = oneParamDTO.getParam();
        log.info("phoneNumber is " + phoneNumber);
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr.append(ran);
        }
        String randomNumber = numStr.toString();

        System.out.println("수신자 번호 : " + phoneNumber);
        System.out.println("인증번호 : " + randomNumber);
        certifiedPhoneNumber(phoneNumber, randomNumber);
        return randomNumber;
    }

    public void certifiedPhoneNumber(String phoneNumber, String cerNum) {

        String api_key = "NCSU7HKA2MDAFFCK";
        String api_secret = "OCLBNOGMNTILNJV04R8DY6T7SXZPQ1XT";
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);    // 수신전화번호
        params.put("from", "01047255788");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "여기모여 인증번호는" + "[" + cerNum + "]" + "입니다.");
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }

    }
}
