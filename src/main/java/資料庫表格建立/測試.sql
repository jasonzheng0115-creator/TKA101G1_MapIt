-- ========================================
-- 測試資料插入腳本
-- ========================================
-- 用途：解決 Foreign Key Constraint 問題
-- 插入測試用的景點和會員資料，方便進行 CRUD 測試
-- ========================================

USE test1;

-- ========== 1. 檢查並插入 REGION 資料 ==========
INSERT IGNORE INTO REGION (REGION_ID, REGION_NAME) VALUES
(1, '北部'),
(2, '中部'),
(3, '南部'),
(4, '東部'),
(5, '離島');

-- ========== 2. 檢查並插入 CATEGORY 資料 ==========
INSERT INTO CATEGORY (CATEGORY_NAME) VALUES
('自然風景'),
('歷史古蹟'),
('美食餐廳'),
('購物商圈'),
('休閒娛樂')
ON DUPLICATE KEY UPDATE CATEGORY_NAME = CATEGORY_NAME;

-- ========== 3. 插入測試景點資料 ==========
-- 如果 ATTRACTION 表為空，插入幾筆測試資料
INSERT INTO ATTRACTION (ATTR_NAME, REGION_ID, CATEGORY_ID, ATTR_ADDRESS, LAT, LNG, ATTR_TEL, OPEN_TIME, IS_OPEN, ATTR_VOTES, ATTR_STARS, AVG_STARS)
VALUES
('台北101', 1, 5, '台北市信義區信義路五段7號', 25.03369140, 121.56460570, '02-8101-8800', '09:00-22:00', '正常營業', 0, 0, 0.0),
('日月潭', 2, 1, '南投縣魚池鄉中山路599號', 23.85700000, 120.91500000, '049-285-5668', '全天開放', '正常營業', 0, 0, 0.0),
('墾丁國家公園', 3, 1, '屏東縣恆春鎮墾丁路596號', 21.94120000, 120.80510000, '08-886-1321', '08:00-17:00', '正常營業', 0, 0, 0.0),
('太魯閣國家公園', 4, 1, '花蓮縣秀林鄉富世291號', 24.15940000, 121.62110000, '03-862-1100', '08:30-17:00', '正常營業', 0, 0, 0.0),
('澎湖跨海大橋', 5, 1, '澎湖縣白沙鄉', 23.66670000, 119.56670000, NULL, '全天開放', '正常營業', 0, 0, 0.0)
ON DUPLICATE KEY UPDATE ATTR_NAME = ATTR_NAME;

-- ========== 4. 插入測試評論資料 ==========
-- 使用測試會員 ID (1001, 1002, 1003)
-- 注意：這些會員 ID 目前不存在於 CUSTOMER 表中，但因為外鍵約束已註解，所以可以插入

-- 取得第一個景點的 ID
SET @first_attr_id = (SELECT ATTR_ID FROM ATTRACTION ORDER BY ATTR_ID LIMIT 1);

-- 插入測試評論
INSERT INTO COMMENT (ATTR_ID, CUST_ID, COMMENT_CONTENT, COMMENT_SCORE, COMMENT_TIME, COMMENT_STATUS)
VALUES
(@first_attr_id, 1001, '這個景點真的很棒！風景優美，值得一遊。', 5, NOW(), '1'),
(@first_attr_id, 1002, '還不錯，但是人太多了，建議平日來訪。', 4, NOW(), '1'),
(@first_attr_id, 1003, '交通方便，適合全家出遊。', 5, NOW(), '1');

-- ========== 5. 驗證資料 ==========
SELECT '========== REGION 表資料 ==========' AS '';
SELECT * FROM REGION;

SELECT '========== CATEGORY 表資料 ==========' AS '';
SELECT * FROM CATEGORY;

SELECT '========== ATTRACTION 表資料（前 5 筆）==========' AS '';
SELECT ATTR_ID, ATTR_NAME, REGION_ID, CATEGORY_ID, ATTR_ADDRESS 
FROM ATTRACTION 
LIMIT 5;

SELECT '========== COMMENT 表資料 ==========' AS '';
SELECT c.COMMENT_ID, c.ATTR_ID, a.ATTR_NAME, c.CUST_ID, c.COMMENT_SCORE, c.COMMENT_STATUS
FROM COMMENT c
JOIN ATTRACTION a ON c.ATTR_ID = a.ATTR_ID;

SELECT '========== 資料插入完成 ==========' AS '';
