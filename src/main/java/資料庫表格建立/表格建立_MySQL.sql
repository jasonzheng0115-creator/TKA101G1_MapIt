-- 指定要使用的資料庫
CREATE DATABASE IF NOT EXISTS test1;
USE test1;

-- =======================================================
-- ⚠️ 刪除舊表 (順序：先刪子表，再刪父表)
-- =======================================================
--DROP TABLE IF EXISTS REPORTS;
--DROP TABLE IF EXISTS ATTR_COLLECT;
--DROP TABLE IF EXISTS COMMENT;
--DROP TABLE IF EXISTS ATTR_IMAGE; -- 新增的圖片子表
--DROP TABLE IF EXISTS ATTR;
--DROP TABLE IF EXISTS CATEGORY;
--DROP TABLE IF EXISTS REGION;

-- =======================================================
-- 1. 建立地區表 (REGION)
-- =======================================================
CREATE TABLE REGION (
    REGION_ID INT NOT NULL PRIMARY KEY,
    REGION_NAME VARCHAR(20)
);

-- =======================================================
-- 2. 建立類別表 (CATEGORY)
-- =======================================================
CREATE TABLE CATEGORY (
    CATEGORY_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    CATEGORY_NAME VARCHAR(50) NOT NULL
);

-- =======================================================
-- 3. 建立景點表 (ATTR)

-- =======================================================
CREATE TABLE ATTR(
     ATTR_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ATTR_NAME VARCHAR(100) NOT NULL,
    REGION_ID INT NOT NULL,
    CATEGORY_ID INT NOT NULL,
    ATTR_ADDRESS VARCHAR(255) NOT NULL,
    LAT DECIMAL(10, 8) NOT NULL,
    LNG DECIMAL(11, 8) NOT NULL,
    ATTR_TEL VARCHAR(255),
    OPEN_TIME VARCHAR(255),
    ATTR_IMG VARCHAR(255),
    IS_OPEN VARCHAR(30) NOT NULL DEFAULT '正常營業',
    ATTR_VOTES INT DEFAULT 0,
    ATTR_STARS INT DEFAULT 0,
    AVG_STARS DECIMAL(2, 1) DEFAULT 0.0,
    
    FOREIGN KEY (REGION_ID) REFERENCES REGION(REGION_ID),
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY(CATEGORY_ID)
);

-- =======================================================
-- 4. 建立景點圖片表 (ATTR_IMAGE) - 輪播圖專用
-- =======================================================
CREATE TABLE ATTR_IMAGE (
    IMG_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    ATTR_ID INT NOT NULL,
    IMG_URL VARCHAR(255) NOT NULL,
    IS_MAIN BOOLEAN DEFAULT FALSE, -- 標記是否為封面縮圖 (1:主圖, 0:其他輪播圖)
    
    FOREIGN KEY (ATTR_ID) REFERENCES ATTR(ATTR_ID) ON DELETE CASCADE
);

-- =======================================================
-- 5. 建立評論表 (COMMENT)
-- =======================================================
CREATE TABLE COMMENT (
    COMMENT_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    ATTR_ID INT NOT NULL,  
    CUST_ID INT NOT NULL,
    COMMENT_CONTENT VARCHAR(1000),
    COMMENT_SCORE TINYINT NOT NULL,
    COMMENT_TIME DATETIME,
    COMMENT_STATUS VARCHAR(10),
    
    FOREIGN KEY (ATTR_ID) REFERENCES ATTRACTION(ATTR_ID) ON DELETE CASCADE
    -- ⚠️ 等組員把 CUSTOMER 表建好後再解開下一行
    -- , FOREIGN KEY (CUST_ID) REFERENCES CUSTOMER(CUST_ID)
);

-- =======================================================
-- 6. 建立景點收藏表 (ATTR_COLLECT)
-- =======================================================
CREATE TABLE ATTR_COLLECT (
    COLLECT_ID INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    ATTR_ID INT NOT NULL,
    CUST_ID INT NOT NULL,  
    COLLECT_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ATTR_ID) REFERENCES ATTR(ATTR_ID) ON DELETE CASCADE
    -- ⚠️ 等組員把 CUSTOMER 表建好後再解開下一行
    -- , FOREIGN KEY (CUST_ID) REFERENCES CUSTOMER(CUST_ID)
);

-- =======================================================
-- 7. 建立檢舉案件表 (REPORTS)
-- =======================================================
CREATE TABLE REPORTS (
    REPORT_ID INT NOT NULL PRIMARY KEY,
    COMMENT_ID INT NOT NULL,
    REPORT_CONTENT VARCHAR(300) NOT NULL,
    REPORT_STATUS VARCHAR(10) NOT NULL DEFAULT '未處理',
    REPORT_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
    CUST_ID INT NOT NULL,
    EMP_ID INT NOT NULL,
    
    FOREIGN KEY (COMMENT_ID) REFERENCES COMMENT(COMMENT_ID) ON DELETE CASCADE
    -- ⚠️ 等組員把 EMPLOYEE 和 CUSTOMER 表建好後再解開以下兩行
    -- , FOREIGN KEY (CUST_ID) REFERENCES CUSTOMER(CUST_ID)
    -- , FOREIGN KEY (EMP_ID) REFERENCES EMPLOYEE(EMP_ID)
);