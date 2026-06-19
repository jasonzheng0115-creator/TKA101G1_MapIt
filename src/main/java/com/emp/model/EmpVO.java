package com.emp.model;

import java.io.Serializable;

import com.dept.model.DeptVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
public class EmpVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "EMP_ID")
    private Integer empId; // 員工編號 (PK)

    @Column(name = "EMP_NAME", nullable = false, length = 10)
    private String empName; // 員工姓名

    @Column(name = "EMP_SEX", nullable = false, length = 1)
    private String empSex; // 性別 ('F'女, 'M'男)

    @Column(name = "EMP_TEL", nullable = false, length = 15)
    private String empTel; // 電話

    @Column(name = "EMP_EMAIL", nullable = false, length = 40)
    private String empEmail; // 電子信箱

    // 多對一：多個員工屬於同一個部門
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID", nullable = false)
    private DeptVO deptVO; // 部門 (FK)

    @Column(name = "EMP_ACC", nullable = false, length = 20)
    private String empAcc; // 帳號

    @Column(name = "EMP_PWD", nullable = false, length = 20)
    private String empPwd; // 密碼

    @Column(name = "EMP_STATUS")
    private Boolean empStatus; // 啟用狀態 (true:啟用, false:停用)


    public EmpVO() {
    }

    public EmpVO(Integer empId, String empName, String empSex, String empTel, String empEmail, 
                 DeptVO deptVO, String empAcc, String empPwd, Boolean empStatus) {
        this.empId = empId;
        this.empName = empName;
        this.empSex = empSex;
        this.empTel = empTel;
        this.empEmail = empEmail;
        this.deptVO = deptVO;
        this.empAcc = empAcc;
        this.empPwd = empPwd;
        this.empStatus = empStatus;
    }

    // ==========================================
    // Getter & Setter 方法
    // ==========================================
    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpSex() {
        return empSex;
    }

    public void setEmpSex(String empSex) {
        this.empSex = empSex;
    }

    public String getEmpTel() {
        return empTel;
    }

    public void setEmpTel(String empTel) {
        this.empTel = empTel;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public DeptVO getDeptVO() {
        return deptVO;
    }

    public void setDeptVO(DeptVO deptVO) {
        this.deptVO = deptVO;
    }

    public String getEmpAcc() {
        return empAcc;
    }

    public void setEmpAcc(String empAcc) {
        this.empAcc = empAcc;
    }

    public String getEmpPwd() {
        return empPwd;
    }

    public void setEmpPwd(String empPwd) {
        this.empPwd = empPwd;
    }

    public Boolean getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(Boolean empStatus) {
        this.empStatus = empStatus;
    }
}