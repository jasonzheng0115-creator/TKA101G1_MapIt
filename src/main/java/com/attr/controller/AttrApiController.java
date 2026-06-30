package com.attr.controller;

import com.attr.model.AttrRepository;
import com.attr.model.AttrVO;
import com.attr.model.AttrImageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attractions")
public class AttrApiController {

    @Autowired
    private AttrRepository attrRepository;

    @GetMapping("/search")
    public List<Map<String, Object>> searchAttractions(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "region", required = false, defaultValue = "") String region) {

        boolean hasKeyword = !keyword.trim().isEmpty();
        boolean hasRegion = !region.trim().isEmpty();

        // 💡 使用團隊內建的分頁機制：限制最多回傳 50 筆，避免一次撈出太多資料拖慢網頁速度
        Pageable pageable = PageRequest.of(0, 50);
        List<AttrVO> rawList = new ArrayList<>();

        // 情境 A：關鍵字與地區都有提供
        if (hasKeyword && hasRegion) {
            try {
                Integer regionId = Integer.valueOf(region);
                Page<AttrVO> page = attrRepository.findByAttrNameContainingAndRegionVO_RegionId(keyword, regionId,
                        pageable);
                rawList = page.getContent();
            } catch (NumberFormatException e) {
                // 如果地區 ID 轉數字失敗，就當作沒這筆
            }
        }
        // 情境 B：只填關鍵字 (例如：搜尋 "鐵塔")
        else if (hasKeyword) {
            Page<AttrVO> page = attrRepository.findByKeywordCombined(keyword, pageable);
            rawList = page.getContent();
        }
        // 情境 C：只選地區
        else if (hasRegion) {
            try {
                Integer regionId = Integer.valueOf(region);
                rawList = attrRepository.findByRegionVO_RegionId(regionId);
            } catch (NumberFormatException e) {
                // 忽略
            }
        }

        // 💡 將 AttrVO 清單包裝成簡單的 Map，安全防範 Jackson 循環參照當機，並優化前端顯示
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (AttrVO attr : rawList) {
            Map<String, Object> map = new HashMap<>();
            map.put("attrId", attr.getAttrId());
            map.put("attrName", attr.getAttrName());
            map.put("attrAddress", attr.getAttrAddress());
            map.put("openTime", attr.getOpenTime());
            map.put("attrVotes", attr.getAttrVotes() != null ? attr.getAttrVotes() : 0);
            map.put("avgStars", attr.getAvgStars() != null ? attr.getAvgStars() : 0.0);

            // 將地區與類別的「中文名稱」直接回傳，配合前端 HTML 顯示
            map.put("regionId", attr.getRegionVO() != null ? attr.getRegionVO().getRegionName() : "未知地區");
            map.put("categoryId", attr.getCategoryVO() != null ? attr.getCategoryVO().getCategoryName() : "未分類");

            // 💡 取得景點圖片：若有主圖或有任何圖片，取得其 imageUrl/imagePath，否則用預設占位圖
            String firstImageUrl = "https://dummyimage.com/450x300/dee2e6/6c757d.jpg";
            if (attr.getImages() != null && !attr.getImages().isEmpty()) {
                AttrImageVO mainImg = null;
                for (AttrImageVO img : attr.getImages()) {
                    if (img.getIsMain() != null && img.getIsMain()) {
                        mainImg = img;
                        break;
                    }
                }
                if (mainImg == null) {
                    mainImg = attr.getImages().iterator().next();
                }
                if (mainImg != null) {
                    if (mainImg.getImageUrl() != null && !mainImg.getImageUrl().trim().isEmpty()) {
                        firstImageUrl = mainImg.getImageUrl();
                    } else if (mainImg.getImagePath() != null && !mainImg.getImagePath().trim().isEmpty()) {
                        firstImageUrl = "/attraction_images/" + mainImg.getImagePath();
                    }
                }
            }
            map.put("firstImageUrl", firstImageUrl);

            resultList.add(map);
        }

        return resultList;
    }
}
