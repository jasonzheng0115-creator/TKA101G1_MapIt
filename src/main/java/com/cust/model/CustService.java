package com.cust.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;

@Service //貼標籤，讓SpringBoot確認接管功能
public class CustService {
	
	@Autowired //讓springBoot自己new，要拿CustRepository實作
	private CustRepository repository;
	
	@Autowired //自動注入EntityManager，並且是去Composite
	private EntityManager entityManager;
	
	//會員登入
	public CustVO login(String account, String password) {
		//最單純只去資料庫看帳號密碼，對了就回傳資料(CustVO)，錯了就回傳null
		return repository.loginCheck(account,password);	
	}
	//會員註冊
	public void register(CustVO custVO) {
		CustVO existId = repository.findByAccount(custVO.getCustAccount());
		if (existId != null) {
			throw new RuntimeException("該帳號已被註冊，請換一個帳號");
		}
		repository.save(custVO); //custVO沒有ID傳進來時，會自動INSERT;custVO有ID時，會自動寫UPDATE
	}
	//會員資料修改
	public void updateProfile(CustVO custVO) {
		System.out.println("更新的會員ID：" + custVO.getCustId());
		System.out.println("更新的會員名字：" + custVO.getCustName());
		repository.save(custVO); //custVO沒有ID傳進來時，會自動INSERT;custVO有ID時，會自動寫UPDATE
		}
	
	//將CustCompositeQuery的條件，用EntityManger把資料庫的資料撈出來
	public List<CustVO> getAll(Map<String,String[]> map){
		return CustCompositeQuery.getAllC(map, entityManager);
	}
	
	//後台員工資料修改
	public CustVO getOneCust(Integer custId) {
		return repository.findByCustId(custId);
	}
	
	
}
