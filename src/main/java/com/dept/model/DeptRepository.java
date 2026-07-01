package com.dept.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptRepository extends JpaRepository<DeptVO, Integer> {

    boolean existsByDeptName(String deptName);

    DeptVO findByDeptName(String deptName);
}
