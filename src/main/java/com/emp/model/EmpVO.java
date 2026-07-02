package com.emp.model;

import java.io.Serializable;

import com.dept.model.DeptVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "EMPLOYEE")
public class EmpVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_ID")
    private Integer empId; // 員工編號 (PK)

    @NotBlank(message = "員工姓名請勿空白")
    @Size(min = 2, max = 20, message = "員工姓名長度必須在2到20字之間")
    @Column(name = "EMP_NAME", nullable = false, length = 20)
    private String empName; // 員工姓名

    @NotBlank(message = "請選擇性別")
    @Column(name = "EMP_SEX", nullable = false, length = 1)
    private String empSex; // 性別 ('F'女, 'M'男)

    @NotBlank(message = "電話請勿空白")
    @Pattern(regexp = "^09\\d{8}$", message = "電話格式錯誤：必須是 09 開頭的 10 碼手機號碼")
    @Column(name = "EMP_TEL", nullable = false, length = 15)
    private String empTel; // 電話

    @NotBlank(message = "信箱請勿空白")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "信箱格式錯誤：請輸入有效的 Email")
    @Column(name = "EMP_EMAIL", nullable = false, length = 40)
    private String empEmail; // 電子信箱

    // 多對一：多個員工屬於同一個部門
    @NotNull(message = "請選擇部門")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID", nullable = false)
    private DeptVO deptVO; // 部門 (FK)

    @NotBlank(message = "帳號請勿空白")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,12}$", message = "帳號格式錯誤：只能是英文字母與數字，且長度必須在6到12碼之間")
    @Column(name = "EMP_ACC", nullable = false, length = 20)
    private String empAcc; // 帳號

    @NotBlank(message = "密碼請勿空白")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "密碼格式錯誤：只能是英文字母與數字，且長度必須在8到20碼之間")
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