package com.trip.controller; // 嚴格遵守：Controller 放在 controller 套件中

import com.cust.model.CustVO;
import com.trip.model.TripVO;
import com.trip.model.TripService; // 匯入剛寫好的 Service
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@Controller
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private com.trip.model.CollabItemService collabItemService;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    // 1. 顯示「我的行程」
    @GetMapping("/my-trips")
    public String showMyTrips(HttpSession session, Model model) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");

        // 把髒活交給 Service
        List<TripVO> myTrips = tripService.getTripsByCustomer(loginCust);

        model.addAttribute("userName", loginCust.getCustName());
        // 將目前登入的會員 ID 傳給前端，用來判斷行程卡片要顯示「刪除」還是「退出編輯」
        model.addAttribute("loginCustId", loginCust.getCustId());
        model.addAttribute("tripList", myTrips);
        return "front-end/trip/my-trips";
    }

    // 2. 顯示「新增行程」的表單
    @GetMapping("/create")
    public String showCreateTripForm(HttpSession session, Model model) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");

        model.addAttribute("userName", loginCust.getCustName());
        model.addAttribute("trip", new TripVO());
        return "front-end/trip/create-trip";
    }

    // 3. 處理「新增行程」的存檔動作
    @PostMapping("/create")
    public String processCreateTrip(TripVO trip, HttpSession session) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");

        // 將存檔與綁定使用者的髒活交給 Service，並取得包含 ID 的新行程
        TripVO savedTrip = tripService.createTrip(trip, loginCust);

        return "redirect:/trip/edit/" + savedTrip.getTripId();
    }

    // 4. 進入特定行程的編輯頁面
    @GetMapping("/edit/{id}")
    public String showEditTripPage(@PathVariable("id") Integer tripId, HttpSession session, Model model) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");

        // 改呼叫我們放寬權限的方法，傳入「行程ID」與「登入者會員ID」
        TripVO trip = tripService.getTripByIdAndPermission(tripId, loginCust.getCustId());

        // 如果找不到行程或沒有權限，踢回列表頁
        if (trip == null) {
            return "redirect:/trip/my-trips";
        }

        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        model.addAttribute("userName", loginCust.getCustName());
        model.addAttribute("trip", trip);
        // 新增這行：將目前登入的會員 ID 傳給編輯頁，用來判定是否顯示管理協作者的權限
        model.addAttribute("loginCustId", loginCust.getCustId());
        return "front-end/trip/edit-trip";
    }

    // 5. 刪除行程 (接收 POST 請求)
    @PostMapping("/delete")
    public String deleteTrip(@RequestParam("tripId") Integer tripId, HttpSession session) {
        // 權限檢查：確認有登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");

        try {
            // 呼叫 Service 執行刪除邏輯 (裡面包含了防呆與刪除子表)
            tripService.deleteTrip(tripId, loginCust.getCustId());
        } catch (RuntimeException e) {
            // 如果 Service 丟出錯誤（例如：找不到行程，或是這個人不是擁有者）
            // 可以選擇印出錯誤，然後導回列表頁
            System.out.println("刪除失敗：" + e.getMessage());
        }

        // 刪除成功（或失敗）後，都重新導回「我的行程」列表頁
        return "redirect:/trip/my-trips";
    }

    // 6. 退出行程協作 (接收來自「我的行程」列表中，協作者卡片的 POST 請求)
    @PostMapping("/exit-collab")
    public String exitCollab(@RequestParam("tripId") Integer tripId, HttpSession session) {
        // 權限檢查：是否登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");

        try {
            // 呼叫服務層方法，傳入「行程ID」與「目前登入者會員ID」進行刪除
            collabItemService.exitCollaboration(tripId, loginCust.getCustId());
        } catch (RuntimeException e) {
            System.out.println("退出協作失敗：" + e.getMessage());
        }

        // 完成退出後，將瀏覽器重新導向回「我的行程」列表頁
        return "redirect:/trip/my-trips";
    }

}