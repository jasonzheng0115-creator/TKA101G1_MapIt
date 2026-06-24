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

import java.util.List;

@Controller
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    // 1. 顯示「我的行程」
    @GetMapping("/my-trips")
    public String showMyTrips(HttpSession session, Model model) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        // ★ 如果沒有登入，導向「請先登入」提示頁面
        if (loginCust == null) {
            // 把「紙條」放進包裹，告訴提示頁面：登入後要跳回 /trip/my-trips
            model.addAttribute("redirectURL", "/trip/my-trips");
            return "front-end/trip/my-trips";
        }

        // 把髒活交給 Service
        List<TripVO> myTrips = tripService.getTripsByCustomer(loginCust);

        model.addAttribute("userName", loginCust.getCustName());
        model.addAttribute("tripList", myTrips);
        return "front-end/trip/my-trips";
    }

    // 2. 顯示「新增行程」的表單
    @GetMapping("/create")
    public String showCreateTripForm(HttpSession session, Model model) {
        if (session.getAttribute("loginCust") == null)
            return "redirect:/login";

        model.addAttribute("trip", new TripVO());
        return "front-end/trip/create-trip";
    }

    // 3. 處理「新增行程」的存檔動作
    @PostMapping("/create")
    public String processCreateTrip(TripVO trip, HttpSession session) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null)
            return "redirect:/login";

        // 將存檔與綁定使用者的髒活交給 Service，並取得包含 ID 的新行程
        TripVO savedTrip = tripService.createTrip(trip, loginCust);

        return "redirect:/trip/edit/" + savedTrip.getTripId();
    }

    // 4. 進入特定行程的編輯頁面
    @GetMapping("/edit/{id}")
    public String showEditTripPage(@PathVariable("id") Integer tripId, HttpSession session, Model model) {
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null)
            return "redirect:/login";

        // 把尋找行程、檢查權限的複雜邏輯全部發包給 Service
        TripVO trip = tripService.getTripByIdAndOwner(tripId, loginCust.getCustId());

        // 如果 Service 說找不到或沒權限 (回傳 null)，就踢回列表頁
        if (trip == null) {
            return "redirect:/trip/my-trips";
        }

        model.addAttribute("trip", trip);
        return "front-end/trip/edit-trip";
    }

    // 5. 刪除行程 (接收 POST 請求)
    @PostMapping("/delete")
    public String deleteTrip(@RequestParam("tripId") Integer tripId, HttpSession session) {
        // 權限檢查：確認有登入
        CustVO loginCust = (CustVO) session.getAttribute("loginCust");
        if (loginCust == null) {
            return "redirect:/login";
        }

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

}