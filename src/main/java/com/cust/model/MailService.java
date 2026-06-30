package com.cust.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired //官方方法JavaMailSender
    private JavaMailSender mailSender;

    public void sendVerificationCode(String receiverEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("MapIt <noreply@mapit.com>");
        message.setTo(receiverEmail);
        message.setSubject("MapIt - 會員註冊驗證碼");
        message.setText("親愛的會員您好：\n\n您的註冊驗證碼為：" + code + "\n\n請在網頁上輸入此驗證碼以完成信箱驗證並啟動帳號。\n\nMapIt 團隊敬上");
        mailSender.send(message);
    }
}
