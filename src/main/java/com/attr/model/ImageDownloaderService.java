package com.attr.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ImageDownloaderService - 圖片下載服務
 * 負責將資料庫中的 imageUrl 下載到本地，並更新 imagePath
 * 
 * 建立日期：2026/06/22
 * 符合 CLAUDE.md 規範：
 * - 使用 com.attr.model 模組
 * - 無 Lombok，使用標準 getter/setter
 * - 方法命名遵循老師規範
 */
@Service
public class ImageDownloaderService {
    
    @Autowired
    private AttrImageRepository attrImageRepository;
    
    // 下載目錄
    private static final String DOWNLOAD_DIR = "C:/upload/attraction_images/";
    
    /**
     * 下載所有圖片到本地
     * 查詢資料庫中 imageUrl 不為空，且 imagePath 為空的所有 AttrImageVO
     * 下載圖片並更新 imagePath
     * 
     * 【重要】如果下載失敗，保留 imageUrl，不清空
     * 這樣前端可以使用 fallback 機制：優先使用本地圖片，失敗則使用線上 URL
     */
    public void downloadImagesToLocal() {
        // 1. 確保下載目錄存在
        File dir = new File(DOWNLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("建立下載目錄: " + DOWNLOAD_DIR);
        }
        
        // 2. 查詢所有需要下載的圖片 (imageUrl 不為空，且 imagePath 為空)
        List<AttrImageVO> imagesToDownload = attrImageRepository.findAll().stream()
            .filter(img -> img.getImageUrl() != null && !img.getImageUrl().trim().isEmpty())
            .filter(img -> img.getImagePath() == null || img.getImagePath().trim().isEmpty())
            .toList();
        
        System.out.println("找到 " + imagesToDownload.size() + " 張圖片需要下載");
        
        // 3. 逐一下載圖片
        int successCount = 0;
        int failCount = 0;
        
        for (AttrImageVO imageVO : imagesToDownload) {
            try {
                String imageUrl = imageVO.getImageUrl();
                System.out.println("開始下載: " + imageUrl);
                
                // 4. 下載圖片
                String filename = downloadImage(imageUrl);
                
                // 5. 更新資料庫：下載成功才清空 imageUrl
                imageVO.setImagePath(filename);
                imageVO.setImageUrl(null);  // 下載成功，清空 imageUrl
                attrImageRepository.save(imageVO);
                
                successCount++;
                System.out.println("下載成功: " + filename);
                
                // 【防爆機制】每張圖片下載後休息 1.5 秒，避免維基百科 429 流量限制
                Thread.sleep(1500);
                
            } catch (Exception e) {
                failCount++;
                System.err.println("下載失敗 [imageId=" + imageVO.getImageId() + "]: " + e.getMessage());
                // 【重要】下載失敗時，不清空 imageUrl，保留線上 URL 作為 fallback
                System.err.println("保留 imageUrl 作為 fallback: " + imageVO.getImageUrl());
            }
        }
        
        System.out.println("下載完成！成功: " + successCount + " 張，失敗: " + failCount + " 張");
        if (failCount > 0) {
            System.out.println("提示：失敗的圖片已保留 imageUrl，前端可使用線上 URL 顯示");
        }
    }
    
    /**
     * 下載單張圖片
     * @param imageUrl 圖片 URL
     * @return 儲存的檔名
     * @throws Exception 下載失敗時拋出
     */
    private String downloadImage(String imageUrl) throws Exception {
        // 1. 先強制轉換為 https
        if (imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }

        // 2. 如果是維基百科的縮圖，直接還原成原始大圖路徑
        if (imageUrl.contains("upload.wikimedia.org/wikipedia/commons/thumb/")) {
            // 移除 /thumb/ 
            imageUrl = imageUrl.replace("/wikipedia/commons/thumb/", "/wikipedia/commons/");
            // 移除最後一個斜線後面所有的縮圖參數 (例如 /640px-xxx.jpg)
            int lastSlashIndex = imageUrl.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                imageUrl = imageUrl.substring(0, lastSlashIndex);
            }
        }
        
        // 3. 產生唯一檔名
        String filename = UUID.randomUUID().toString() + ".jpg";
        String filepath = DOWNLOAD_DIR + filename;
        
        // 4. 建立 HTTPS 連線（使用加密協定）
        URL url = new URL(imageUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);  // 連線逾時 10 秒
        connection.setReadTimeout(10000);     // 讀取逾時 10 秒
        
        // 設定 User-Agent，避免某些網站阻擋
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        // 設定 Referer，假裝從維基百科點進去，避免被阻擋
        connection.setRequestProperty("Referer", "https://commons.wikimedia.org/");
        
        // 5. 讀取圖片資料並寫入檔案
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filepath)) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        
        // 6. 回傳檔名
        return filename;
    }
}
