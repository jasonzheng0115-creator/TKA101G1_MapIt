package com.dept.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DEPARTMENT")
public class DeptVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPT_ID")
    private Integer deptId; // 部門編號 (PK, Auto Increment)

    @Column(name = "DEPT_NAME", nullable = false, length = 20)
    private String deptName; // 部門名稱


    public DeptVO() {
    }

    public DeptVO(Integer deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

    // ==========================================
    // Getter & Setter 方法
    // ==========================================
    
	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
