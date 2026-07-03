# MapIt 專案：兩日開發與優化工作日誌 (2026/07/01 ~ 2026/07/03)

本文件總結了過去兩天我們針對 **「後台員工管理與權限安全」** 以及 **「前台行程編輯與使用者體驗 (UX)」** 進行的重構、除錯與優化成果。

---

## 🛠️ 開發與優化項目詳情

### 一、 後台員工管理：安全防禦與欄位控制

1. **員工編輯權限細緻化分流 (自己 vs 他人)**
   * **他人資料**：除登入者本人外的員工資料皆設為唯讀（`readonly` / `disabled`），且**隱藏密碼與確認密碼欄位**，杜絕越權修改他人密碼的漏洞。
   * **自己資料**：允許本人修改姓名、電話、信箱、密碼，但將「帳號」、「部門」與「狀態」鎖定為唯讀，防止員工自行提升權限或任意轉移部門。
   * **密碼重設**：若本人編輯資料時未填寫密碼，後端會保持原密碼，避免空值覆蓋。
   * **排版優化**：變更密碼與確認密碼欄位改為在同一側垂直上下排列，視覺更為美觀整齊。

2. **後台最高權限安全密碼 `root888` 校驗機制**
   * **前端阻擋**：當員工欲修改自己或其他人的「部門」或「狀態」時，JavaScript 會自動攔截並彈出 `prompt` 詢問安全密碼。輸入不符 `root888` 或取消則直接 `preventDefault` 阻擋表單送出（頁面上不呈現任何有關安全密碼的輸入框與字樣，隱蔽性極高）。
   * **後端防禦**：在 `EmpController` 的 `/update` 路由加載安全密碼的後端二次校驗，防止繞過前端發送惡意請求。

---

### 二、 表單驗證與舊 Servlet 邏輯移植

1. **Java Bean Validation 驗證框架移植**
   * 於 `EmpVO.java` 各屬性套用 `jakarta.validation.constraints` 註解，完美重現舊 Servlet 的所有規則：
     * **姓名**：2-20字，不可空白。
     * **電話**：必須為 10 碼且 09 開頭（正規表達式 `^09\d{8}$`）。
     * **信箱**：標準格式，不可空白（正規表達式 `^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$`）。
     * **部門**：不可為空。
     * **帳號**：6-12字英數，不可空白。
     * **密碼**：8-20字英數，不可空白。

2. **關聯欄位驗證錯誤綁定修正**
   * 解決了許多 Spring Validation 中 Mult-To-One 關聯對象的錯誤顯示 Bug。將 Thymeleaf 錯誤對象從子屬性 `deptVO.deptId` 改為關聯實體屬性本身 `deptVO`，使「請選擇部門」驗證失敗時能正確於前端渲染出紅色錯誤訊息。

3. **點擊輸入框自動清除錯誤提示**
   * 在新增與修改員工頁面中，加入了 JavaScript 監聽器。當欄位出現紅色錯誤字眼時，只要使用者聚焦（`focus`）或點擊該輸入框準備重新編輯，該欄位下方的紅色錯誤訊息與框線就會立刻自動消失。

---

### 三、 路由優化與重設按鈕自訂化

1. **路由路徑一致性**
   * 將新增員工的 POST 路由修改為與 GET 相同的 `/emp/addEmp`。當後端驗證失敗回傳時，瀏覽器網址列能完美維持在原 URL，不再變更為內部的 `/emp/insert`。
   * 修正新增成功後的 Redirect 重定向目標路徑為 `/emp/listAllEmp`，解決了原先 404 資源找不到的 Bug。

2. **自訂一鍵清空 (Reset) 函式**
   * 由於 Validation 回傳原頁面時錯誤資料已被注入為 `value`，原生的 HTML Reset 無效。我們在 [addEmp.html](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/resources/templates/back-end/emp/addEmp.html) 中實作了自訂的 `clearForm()` JS 函式，點選重設時能真正清空所有欄位值與錯誤提示。

---

### 四、 前台行程編輯：視覺效果與 UX 體驗提升

1. **「雙頭像重疊」Bug 徹底根除**
   * **原問題**：前台行程列表中，擁有者/協作者頭像會出現一列圖片、一列中文字首頭像，兩者同時並存且重疊錯位。原因為 JS `onload` 觸發受瀏覽器快取與載入時序影響而失效。
   * **解決方案**：全面捨棄 JavaScript 動態隱藏，改為 **Thymeleaf 服務端純後端條件渲染（`th:if` / `th:unless`）**。有頭像圖片時僅生成 `<img>` 標籤，無圖片時僅生成首字 `<div>` 標籤，100% 確保只呈現一種頭像。

2. **景點卡片顯示營業時間**
   * 在左側景點卡片的地址下方新增顯示原始營業時間（例如：`09:00-18:00` 或 `24小時開放`），資料庫欄位透過 `TripItemService` 完整封裝傳遞，資訊更為豐富。

3. **卡片儲存按鈕動態配色優化**
   * 個別景點卡片右下角的「儲存」按鈕背景色，會動態提取並套用當天行程的主色調（如 `DAY 1` 為玫瑰粉 `#D36A6A`、`DAY 2` 為鼠尾草綠 `#5A9E5A`）。既融入天數色彩、避免與卡片背景色混淆，又不與頂部的橘色「儲存資訊」按鈕撞色。同時新增磁碟片圖標並微調按鈕內距。

4. **儲存成功靜音與天數/色彩無縫更新**
   * **即時更新**：當修改景點時間（如改到第二天）並儲存後，或是修改最上方出發日期並儲存後，JavaScript 會立即重載 `loadTripItems()`，在不重新整理網頁的情況下，卡片天數、顏色配色與 Google 地圖連線會**當下立刻自動變換**。
   * **靜音體驗**：移成了儲存成功時的 `alert()` 彈跳視窗，只在儲存失敗時顯示錯誤彈窗，讓使用者的編輯體驗更為流暢安靜。

5. **新增景點時間自動預設**
   * 在景點探索搜尋景點並點選「加入」行程時，系統會自動將該景點的預設到達/離開時間設為該行程第一天的凌晨 `00:00`，省去使用者空白手動設定的麻煩。

---

### 💾 修改與異動的檔案總覽

* **後端控制器與服務層**：
  * [EmpController.java](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/java/com/emp/controller/EmpController.java) (後端安全性驗證、路由調整)
  * [EmpVO.java](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/java/com/emp/model/EmpVO.java) (Validation 規則移植)
  * [TripItemService.java](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/java/com/trip/model/TripItemService.java) (新增預設時間、營業時間傳遞)
* **前端頁面與範本**：
  * [addEmp.html](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/resources/templates/back-end/emp/addEmp.html) (Reset優化、點選清除錯誤、部門錯誤路徑)
  * [update_emp_input.html](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/resources/templates/back-end/emp/update_emp_input.html) (唯讀限制、安全密碼提示、密碼排版)
  * [my-trips.html](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/resources/templates/front-end/trip/my-trips.html) (Thymeleaf 後端頭像渲染優化)
  * [edit-trip.html](file:///c:/TKA101_WebApp/eclipse_workspace1/TKA101G1_MapIt/src/main/resources/templates/front-end/trip/edit-trip.html) (營業時間顯示、天數動態色彩儲存按鈕、靜音成功儲存、即時更新)
