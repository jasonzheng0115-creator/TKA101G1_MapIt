package com.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.message.model.MessageService;

import java.io.IOException;

@Controller
@RequestMapping("/emp/message")
public class EmpMessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/send")
    public String showSendForm() {
        return "back-end/message/messageSend";
    }

    @PostMapping("/send")
    public String processSend(
            @RequestParam("msgHeadline") String msgHeadline,
            @RequestParam("msgContent") String msgContent,
            @RequestParam(value = "msgPic", required = false) MultipartFile msgPic,
            @RequestParam(value = "sendToAll", required = false) String sendToAll,
            RedirectAttributes redirectAttributes) {

        try {
            String msgPicPath = null;
            if (msgPic != null && !msgPic.isEmpty()) {
                // 取得家目錄並建立專屬資料夾
                String userHome = System.getProperty("user.home");
                String uploadDirectory = userHome + java.io.File.separator + "upload" + java.io.File.separator + "messagePic";
                java.io.File folder = new java.io.File(uploadDirectory);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                // 產生唯一檔名
                String originalFilename = msgPic.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = "msg_" + System.currentTimeMillis() + fileExtension;
                java.io.File saveFile = new java.io.File(uploadDirectory, newFileName);
                
                // 存檔至本地端硬碟
                msgPic.transferTo(saveFile);
                // 取得絕對路徑準備存入資料庫
                msgPicPath = saveFile.getAbsolutePath();
            }

            if ("true".equals(sendToAll)) {
                messageService.sendNotificationToAll(msgHeadline, msgContent, msgPicPath);
                redirectAttributes.addFlashAttribute("successMsg", "已成功發送通知給全體會員！");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "目前僅支援全體發送，請勾選全體會員選項！");
                return "redirect:/emp/message/send";
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "圖片處理失敗！");
            e.printStackTrace();
        }

        return "redirect:/emp/message/send";
    }
}
