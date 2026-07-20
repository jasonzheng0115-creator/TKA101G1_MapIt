# 🗺️ 旅圖 MapIt (TKA101G1)

> 專為探索全台景點、規劃個人化行程與購買門票票券而設計的綜合性旅遊平台。

**MapIt** 是一個基於 Spring Boot 開發的線上景點導覽、行程規劃與旅遊票券預訂系統。我們致力於提供完整的前台顧客瀏覽與購物體驗，並為管理者打造了嚴謹且直覺的後台管理介面。

---

## ✨ 系統特色功能 (Features)

### 🛍️ 前台系統 (Frontend - 會員端)
* **📍 景點探索與互動**：提供視覺化熱門景點動態輪播、關鍵字/名稱精準搜尋，以及全台區域與縣市兩級動態篩選。使用者可將喜愛景點加入「我的最愛」、撰寫評論留言，系統亦提供不當評價的檢舉回報機制。
* **👤 會員中心**：支援會員註冊與登入驗證、個人檔案與密碼維護、歷史訂單紀錄查詢，並具備個人專屬票券匣及通知中心。
* **🗺️ 協作行程規劃**：提供新增與編輯旅遊行程功能，並支援權限設定，可邀請好友加入旅遊群組共同編輯與規劃。
* **🛒 高效商城系統**：提供各類旅遊商品與門票瀏覽、類別檢索及購買功能，結合 **Redis** 提供高效順暢的購物車暫存與線上結帳體驗。

### ⚙️ 後台系統 (Backend - 管理員端)
* **👥 人事與權限管理**：員工資料維護、部門權限設定與後台登入控管。
* **🧑‍🤝‍🧑 會員與檢舉管理**：檢視所有註冊會員帳號狀態，並提供前台使用者評價與留言之檢舉審核機制與維護。
* **🏞️ 景點與分類管理**：全台景點上架、熱門推薦設定、景點與商品類別維護及評論管理。
* **🤝 供應商與對帳**：合作供應商資料維護、供應商應付帳款 (Accounts Payable) 對帳與結算。
* **🎫 商品與訂單管理**：旅遊商品與電子票券的上架、下架維護、庫存管理，以及處理顧客訂單、查詢與交易狀態追蹤。

---

## 🛠️ 技術棧 (Tech Stack)

### Backend
- **Core:** Java 17, Spring Boot
- **Data Access:** Spring Data JPA, Hibernate
- **Utilities:** Lombok, Gson / Jackson, Maven

### Frontend
- **Template Engine:** Thymeleaf
- **UI/Styling:** Bootstrap, HTML / CSS / JavaScript

### Database & DevOps
- **Database:** MySQL 8.x
- **Cache:** Redis (用於前台購物車、暫存與系統快取)
- **Containerization:** Docker, Docker Compose (容器化一鍵部署環境)

### Third-Party Services & APIs
- **QR Code:** ZXing (電子票券生成)
- **Email Service:** JavaMail (Gmail SMTP 寄信服務)
- **Map Service:** Google Maps API

---

## 📂 專案結構與模組劃分 (Project Structure)

系統採用模組化開發，職責劃分明確。以下為主要模組與負責人對照表：

| 模組 (Package) | 功能描述 | 負責人 |
| :--- | :--- | :--- |
| `com.index` | 前後台首頁與導向模組 | 魏森文、鄭瑋儒 |
| `com.cust` | 前台會員功能與後台會員管理模組 | 陳㛄伶 |
| `com.dept` | 後台部門管理模組 | 鄭瑋儒 |
| `com.emp` | 後台員工管理模組 | 鄭瑋儒 |
| `com.splr` | 合作供應商管理模組 | 林曉萱 |
| `com.ap` | 供應商應付帳款對帳模組 | 林曉萱 |
| `com.attr` | 景點資訊與導覽模組 | 魏森文 |
| `com.category` | 景點與商品分類模組 | 魏森文 |
| `com.region` | 景點區域與縣市地圖分類模組 | 魏森文 |
| `com.trip` | 自訂行程與遊程規劃模組 | 鄭瑋儒 |
| `com.prod` | 旅遊商品與遊程模組 | 林曉萱 |
| `com.ticket` | 票券與電子票券 (QR Code) 模組 | 陳㛄伶 |
| `com.orders` | 訂單與交易管理模組 | 林曉萱 |
| `com.fav` | 我的收藏清單模組 | 魏森文 |
| `com.comment` | 景點與商品評價 / 留言檢舉與審核模組 | 魏森文 |
| `com.reports` | 評價/留言檢舉與審核模組 | 魏森文 |
| `com.message` | 站內訊息與通知模組 | 陳㛄伶 |

---

## 👥 開發團隊 (Team)
**TKA101 第一組** 
我們是由四位專注於軟體開發的 Java 工程師所組成的團隊，共同合作完成此系統的設計與實作。
