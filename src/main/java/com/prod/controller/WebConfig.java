package com.prod.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 1. 當網頁網址輸入 /upload/圖片檔名.png 時
		// 2. 實際上會去電腦實體硬碟的 file:C:/upload/products/ 底下找檔案
		registry.addResourceHandler("/upload/**")
				.addResourceLocations("file:C:/upload/products/");
	}
}
