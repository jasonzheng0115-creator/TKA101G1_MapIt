package com.index.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig - Spring MVC 設定類別
 * 
 * 用途：設定靜態資源路徑映射，讓上傳的圖片可以透過 URL 存取
 * 例如：實體路徑 C:/upload/attraction_images/photo.jpg
 * 可透過 http://localhost:8080/attraction_images/photo.jpg 存取
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

        /**
         * 設定靜態資源處理器
         * 將 /attraction_images/** 的 URL 請求映射到實體檔案路徑
         */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        String userHome = System.getProperty("user.home");

	        // 映射景點圖片路徑：/attraction_images/** -> file:<user.home>/upload/attraction_images/
	        registry.addResourceHandler("/attraction_images/**")
	                        .addResourceLocations("file:" + userHome.replace("\\", "/") + "/upload/attraction_images/");

	        // 映射 /uploads/** 到本地硬碟路徑（整合自 com.attr.config.WebMvcConfig）
	        registry.addResourceHandler("/uploads/**")
	                        .addResourceLocations("file:" + userHome.replace("\\", "/") + "/upload/attraction_images/");

	        // 保留預設的靜態資源路徑（/static/, /public/, /resources/）
	        registry.addResourceHandler("/static/**")
	                        .addResourceLocations("classpath:/static/");
	}

}
