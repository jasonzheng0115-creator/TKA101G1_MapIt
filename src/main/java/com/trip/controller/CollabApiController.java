package com.trip.controller;

import com.cust.model.CustVO;
import com.cust.model.CustRepository;
import com.trip.model.CollabItemService;
import com.trip.model.CollabItemVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // 標註這是專門回傳 JSON 資料的 API 控制器
@RequestMapping("/api/collabs") // 統一此控制器的網址開頭為 /api/collabs
public class CollabApiController {

    @Autowired
    private CollabItemService collabItemService;

    @Autowired
    private com.cust.model.CustRepository custRepository;

    // 1. 撈取特定行程的所有協作者 (GET /api/collabs/{tripId})
    @GetMapping("/{tripId}")
    public ResponseEntity<?> getCollaborators(@PathVariable Integer tripId, HttpSession session) {
        // 權限檢查：確認有登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return ResponseEntity.status(401).body("請先登入"); // 回傳 401 未授權
        }

        // 權限檢查：確認目前登入者對此行程具有編輯權限（建立者或協作者）
        if (!collabItemService.hasEditPermission(tripId, loginCust.getCustId())) {
            return ResponseEntity.status(403).body("你沒有權限檢視此行程的協作者"); // 回傳 403 被拒絕
        }

        try {
            // 呼叫 Service 取得完整的協作者實體物件清單
            List<CollabItemVO> collabs = collabItemService.getCollaborators(tripId);

            // 💡 新手友善設計：使用簡單的 for 迴圈來過濾欄位，避免 Circular Reference (循環參考) 導致序列化 JSON 失敗！
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (CollabItemVO c : collabs) {
                Map<String, Object> map = new HashMap<>();
                map.put("collabId", c.getCollabId());
                map.put("custId", c.getCustVO().getCustId());
                map.put("custName", c.getCustVO().getCustName());
                map.put("custAccount", c.getCustVO().getCustAccount());
                resultList.add(map); // 將過濾後的 Map 資料放入清單
            }

            return ResponseEntity.ok(resultList); // 回傳 JSON 陣列給前端
        } catch (Exception e) {
            return ResponseEntity.status(400).body("載入失敗：" + e.getMessage());
        }
    }

    // 2. 新增協作者 (POST /api/collabs/{tripId}/add)
    @PostMapping("/{tripId}/add")
    public ResponseEntity<?> addCollaborator(@PathVariable Integer tripId,
            @RequestBody Map<String, String> requestData,
            HttpSession session) {
        // 權限檢查：確認有登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        // 從前端傳來的 JSON 包裹中取得要新增的「帳號」
        String custAccount = requestData.get("custAccount");
        if (custAccount == null || custAccount.trim().isEmpty()) {
            return ResponseEntity.status(400).body("請輸入協作者帳號");
        }

        try {
            // 呼叫 Service 透過帳號進行查詢並新增 (該方法內部已寫好「必須是行程擁有者」與「不能重複加」的防護)
            CollabItemVO newCollab = collabItemService.addCollaboratorByAccount(tripId, custAccount,
                    loginCust.getCustId());

            // 包裝要回傳給前端的資料
            Map<String, Object> map = new HashMap<>();
            map.put("collabId", newCollab.getCollabId());
            map.put("custId", newCollab.getCustVO().getCustId());
            map.put("custName", newCollab.getCustVO().getCustName());
            map.put("custAccount", newCollab.getCustVO().getCustAccount());

            return ResponseEntity.ok(map);
        } catch (Exception e) {
            // 如果拋出例外（例如找不到此帳號），直接把錯誤訊息回傳給前端
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // 3. 移除協作者 (DELETE /api/collabs/{collabId})
    @DeleteMapping("/{collabId}")
    public ResponseEntity<?> removeCollaborator(@PathVariable Integer collabId, HttpSession session) {
        // 權限檢查：確認有登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        try {
            // 透過我們在步驟 2 寫的方法找出該協作紀錄
            CollabItemVO collab = collabItemService.getCollabById(collabId);
            if (collab == null) {
                return ResponseEntity.status(404).body("找不到此協作紀錄");
            }

            // 權限檢查：只有行程「擁有者（建立者）」才可以主動移除其他人！
            if (!collab.getTripVO().getCustVO().getCustId().equals(loginCust.getCustId())) {
                return ResponseEntity.status(403).body("只有行程擁有者可以管理共同編輯人！");
            }

            // 執行移除
            collabItemService.removeCollaborator(collabId);
            return ResponseEntity.ok("移除成功");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("移除失敗：" + e.getMessage());
        }
    }

    // 4. 關鍵字搜尋會員帳號 (GET /api/collabs/search-users)
    @GetMapping("/search-users")
    public ResponseEntity<?> searchUsers(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        if (keyword.trim().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // 搜尋包含關鍵字的帳號
        List<CustVO> list = custRepository.findByCustAccountContaining(keyword);

        // 只過濾出必要的屬性以回傳前端，防止 Jackson 序列化出錯 (Circular Reference)
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 限制顯示前 10 筆，避免下拉清單過長
        int limit = Math.min(list.size(), 10);
        for (int i = 0; i < limit; i++) {
            CustVO cust = list.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("custId", cust.getCustId());
            map.put("custName", cust.getCustName());
            map.put("custAccount", cust.getCustAccount());
            resultList.add(map);
        }

        return ResponseEntity.ok(resultList);
    }

}
