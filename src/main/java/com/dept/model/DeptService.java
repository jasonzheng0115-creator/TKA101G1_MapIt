package com.dept.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeptService {

    @Autowired
    private DeptRepository deptRepository;

    // 查詢所有部門
    public List<DeptVO> getAll() {
        return deptRepository.findAll();
    }

    // 新增或修改部門資料
    public void save(DeptVO deptVO) {
        deptRepository.save(deptVO);
    }

    // 依部門編號查詢單一部門資料
    public DeptVO getOneDept(Integer deptId) {
        // 使用 findById 會回傳一個 Optional 禮物盒，避免 NullPointerException (NPE)
        Optional<DeptVO> optional = deptRepository.findById(deptId);
        return optional.orElse(null); // 如果有部門就拿出來，沒有就回傳 null
    }

    // 檢查部門名稱是否重複
    public boolean isDeptNameDuplicate(String deptName) {
        return deptRepository.existsByDeptName(deptName);
    }

    // 依部門名稱查詢
    public DeptVO findByDeptName(String deptName) {
        return deptRepository.findByDeptName(deptName);
    }
}
