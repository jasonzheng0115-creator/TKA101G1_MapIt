package com.prod.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
	// ====== 以下方法對沒有C槽的電腦會早成錯誤 ======	
		// 1. 當網頁網址輸入 /upload/圖片檔名.png 時
		// 2. 實際上會去電腦實體硬碟的 file:C:/upload/products/ 底下找檔案
//		registry.addResourceHandler("/upload/**")
//				.addResourceLocations("file:C:/upload/products/");
		
		
	// ====== 更改為用動態絕對路徑 ======
		// 動態取得當前專案根目錄的絕對路徑
		String userHome = System.getProperty("user.home");
		// 把上傳路徑設定在專案目錄底下的 upload/products 資料夾
		// 用 / 替代 \ 確保跨平台相容性
		String uploadPath = "file:" + userHome.replace("\\", "/") + "/upload/products/";
		
		// 虛擬路徑對應
		registry.addResourceHandler("/upload/**").addResourceLocations(uploadPath);
		
	}
}
