package com.attr.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OpenDataDto - 用於接收 Open Data API 的 JSON 資料
 * 使用 @JsonIgnoreProperties(ignoreUnknown = true) 忽略不需要的欄位
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenDataDto {
    
    // ========== 屬性欄位 ==========
    @JsonProperty("ScenicSpotName")
    private String ScenicSpotName;
    
    @JsonProperty("DescriptionDetail")
    private String DescriptionDetail;
    
    @JsonProperty("Address")
    private String Address;
    
    @JsonProperty("Picture")
    private Map<String, String> Picture;
    
    // ========== 建構子 ==========
    public OpenDataDto() {
        super();
    }
    
    // ========== Getter & Setter ==========
    
    public String getScenicSpotName() {
        return ScenicSpotName;
    }
    
    public void setScenicSpotName(String scenicSpotName) {
        ScenicSpotName = scenicSpotName;
    }
    
    public String getDescriptionDetail() {
        return DescriptionDetail;
    }
    
    public void setDescriptionDetail(String descriptionDetail) {
        DescriptionDetail = descriptionDetail;
    }
    
    public String getAddress() {
        return Address;
    }
    
    public void setAddress(String address) {
        Address = address;
    }
    
    public Map<String, String> getPicture() {
        return Picture;
    }
    
    public void setPicture(Map<String, String> picture) {
        Picture = picture;
    }
    
    // ========== toString (方便除錯) ==========
    @Override
    public String toString() {
        return "OpenDataDto [ScenicSpotName=" + ScenicSpotName + ", DescriptionDetail=" + DescriptionDetail 
                + ", Address=" + Address + ", Picture=" + Picture + "]";
    }
}
