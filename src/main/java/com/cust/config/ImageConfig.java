package com.cust.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//一定要有這個程式碼，才能讀取專案外的檔案路徑資料
@Configuration // 開機配置標籤，Spring Boot一開機就會讀這份線路圖
// WebMvcConfigurer代表要擴充伺服器的走道設定
public class ImageConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 取得當前電腦此專案資料夾絕對路徑 ⬇︎存進"user.dir"=projectPath
		String projectPath = System.getProperty("user.dir");
		// 取得前端路徑，只要網址是/uploads/...開頭=
		registry.addResourceHandler("/uploads/**")
				// 去本機的專案/uploads拿照片 file:是通訊協定告訴伺服器，要去本機的實體硬碟資料夾拿檔案
				.addResourceLocations("file:" + projectPath + "/uploads");
	}

}
