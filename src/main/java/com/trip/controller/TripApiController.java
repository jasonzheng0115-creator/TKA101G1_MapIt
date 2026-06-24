package com.trip.controller;

import com.cust.model.CustVO;
import com.trip.model.TripService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/* 專門處理 行程主體 (TripVO) 的 JSON 資料更新。*/
@RestController // 標註這是專門回傳資料 (JSON 或純文字) 的 API 控制器
@RequestMapping("/api/trips") // 統一這個控制器的網址開頭
public class TripApiController {

    @Autowired
    private TripService tripService;

    // 1. 更新行程基本資訊
    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTripInfo(@PathVariable Integer tripId,
            @RequestBody Map<String, String> requestData,
            HttpSession session) {

        // 權限檢查：是否登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return ResponseEntity.status(401).body("請先登入"); // 401 Unauthorized
        }

        try {
            // 從前端傳來的 JSON 包裹中取出資料
            String tripName = requestData.get("tripName");
            String tripDateStr = requestData.get("tripDate");
            Boolean tripStatus = Boolean.valueOf(requestData.get("tripStatus"));

            // 把字串轉換成資料庫需要的 java.sql.Date 格式
            java.sql.Date tripDate = null;
            if (tripDateStr != null && !tripDateStr.isEmpty()) {
                tripDate = java.sql.Date.valueOf(tripDateStr);
            }

            // 呼叫我們tripService
            tripService.updateTripInfo(tripId, tripName, tripDate, tripStatus, loginCust.getCustId());

            return ResponseEntity.ok("更新成功");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("更新失敗：" + e.getMessage());
        }
    }
}
