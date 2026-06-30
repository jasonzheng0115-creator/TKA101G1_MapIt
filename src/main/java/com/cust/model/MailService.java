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
    
    // 新增忘記密碼的寄信功能
    public void sendNewPassword(String receiverEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("MapIt <noreply@mapit.com>");
        message.setTo(receiverEmail);
        message.setSubject("MapIt - 會員密碼重置通知");
        message.setText("親愛的會員您好：\n\n您的密碼已成功重置。\n\n【您的新密碼為：" + newPassword + "】\n\n請使用此密碼登入，並建議您登入後盡速修改為您熟悉的密碼。\n\nMapIt 團隊敬上");
        mailSender.send(message);
    }
}
