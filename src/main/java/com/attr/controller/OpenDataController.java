package com.attr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.attr.model.OpenDataService;

@RestController
public class OpenDataController {

    @Autowired
    private OpenDataService openDataService;

    @GetMapping("/admin/import")
    public String triggerImport() {
        int count = openDataService.importAttractions();
        return "匯入完成，共新增 " + count + " 筆資料！";
    }
}