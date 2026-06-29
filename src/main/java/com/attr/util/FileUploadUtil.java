package com.attr.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

/**
 * FileUploadUtil - 檔案上傳工具類別
 * 負責處理檔案上傳、儲存、刪除等操作
 * 
 * 更新說明 (2026/06/22)：
 * - 從 com.image.util.FileUploadUtil 重構為 com.attr.util.FileUploadUtil
 * - 符合 CLAUDE.md 專案規範：使用 com.attr 模組
 */
public class FileUploadUtil {
    
    // 檔案上傳根目錄
    private static final String UPLOAD_DIR = "C:/upload/attraction_images/";
    
    /**
     * 上傳檔案到指定目錄
     * @param file 上傳的檔案
     * @return 儲存後的檔案相對路徑 (例如: "abc123.jpg")
     * @throws IOException 檔案操作失敗時拋出
     */
    public static String uploadFile(MultipartFile file) throws IOException {
        // 檢查檔案是否為空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上傳檔案不可為空");
        }
        
        // 確保上傳目錄存在
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 取得原始檔名與副檔名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 使用 UUID 產生唯一檔名
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 儲存檔案
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 回傳相對路徑 (只回傳檔名，不含完整路徑)
        return newFilename;
    }
    
    /**
     * 刪除指定檔案
     * @param filename 檔案名稱 (相對路徑)
     * @return 是否刪除成功
     */
    public static boolean deleteFile(String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                return false;
            }
            
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 取得檔案的完整路徑
     * @param filename 檔案名稱 (相對路徑)
     * @return 完整路徑
     */
    public static String getFullPath(String filename) {
        return UPLOAD_DIR + filename;
    }
    
    /**
     * 檢查檔案是否存在
     * @param filename 檔案名稱 (相對路徑)
     * @return 是否存在
     */
    public static boolean fileExists(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        return Files.exists(filePath);
    }
    
    /**
     * 驗證檔案類型是否為圖片
     * @param file 上傳的檔案
     * @return 是否為圖片
     */
    public static boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    
    /**
     * 取得上傳目錄路徑
     * @return 上傳目錄路徑
     */
    public static String getUploadDir() {
        return UPLOAD_DIR;
    }
}
