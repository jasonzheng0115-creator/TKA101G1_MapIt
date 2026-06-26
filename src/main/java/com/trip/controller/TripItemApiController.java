package com.trip.controller;

import com.cust.model.CustVO;
import com.trip.model.TripItemService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip-items")
public class TripItemApiController {

    @Autowired
    private TripItemService tripItemService;

    // 接收前端加入景點的請求
    @PostMapping("/add")
    public Map<String, Object> addArrcToTrip(
            @RequestParam("tripId") Integer tripId,
            @RequestParam("attrId") Integer attrId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 將髒活全部交給 Service 處理
            tripItemService.addArrcToTrip(tripId, attrId);

            response.put("success", true);
            response.put("message", "成功加入行程！");
        } catch (Exception e) {
            // 如果 Service 拋出例外，這裡負責捕捉並回傳錯誤訊息給前端
            response.put("success", false);
            response.put("message", "加入失敗：" + e.getMessage());
        }

        return response;
    }

    // 接收前端讀取明細清單的請求
    @GetMapping("/{tripId}")
    public List<Map<String, Object>> getTripItems(@PathVariable("tripId") Integer tripId) {
        // 直接向 Service 索取已經包裝好的精美包裹
        return tripItemService.getTripItemsFormatForFrontend(tripId);
    }

    // 3. 刪除單一景點明細
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteTripItem(@PathVariable Integer itemId, HttpSession session) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return ResponseEntity.status(401).body("請先登入"); // 401 未授權
        }

        try {
            tripItemService.deleteTripItem(itemId, loginCust.getCustId());
            return ResponseEntity.ok("刪除成功");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 沒有權限
        }
    }

    // 4. 更新景點明細的時間與備註
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateTripItem(@PathVariable Integer itemId,
            @RequestBody Map<String, String> requestData,
            HttpSession session) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        try {
            // 從前端傳來的 JSON 裡面取出資料
            String arrivalTimeStr = requestData.get("arrivalTime");
            String depTimeStr = requestData.get("depTime");
            String itemNote = requestData.get("itemNote");

            // 把字串轉換成 LocalDateTime
            java.time.LocalDateTime arrTime = (arrivalTimeStr != null && !arrivalTimeStr.isEmpty())
                    ? java.time.LocalDateTime.parse(arrivalTimeStr)
                    : null;
            java.time.LocalDateTime depTime = (depTimeStr != null && !depTimeStr.isEmpty())
                    ? java.time.LocalDateTime.parse(depTimeStr)
                    : null;

            // 呼叫 Service
            tripItemService.updateTripItemDetails(itemId, arrTime, depTime, itemNote, loginCust.getCustId());

            return ResponseEntity.ok("更新成功");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("更新失敗：" + e.getMessage());
        }
    }

    // 5. 拖拉排序景點明細 (POST /api/trip-items/reorder)
    @PostMapping("/reorder")
    public ResponseEntity<?> reorderTripItems(@RequestBody List<Integer> itemIds, HttpSession session) {
        // 權限檢查：確認有登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        try {
            // 呼叫 Service 執行重新排序
            tripItemService.reorderTripItems(itemIds, loginCust.getCustId());
            return ResponseEntity.ok("排序更新成功");
        } catch (Exception e) {
            // 捕捉任何異常（例如權限不足或非法跨行程修改），並回傳給前端
            return ResponseEntity.status(400).body("排序更新失敗：" + e.getMessage());
        }
    }

}