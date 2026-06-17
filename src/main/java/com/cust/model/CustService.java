package com.cust.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //貼標籤，讓SpringBoot確認接管功能
public class CustService {
	
	@Autowired //讓springBoot自己new，要拿CustRepository實作
	private CustRepository repository;
	
	//會員登入
	public CustVO login(String account, String password) {
		//最單純只去資料庫看帳號密碼，對了就回傳資料(CustVO)，錯了就回傳null
		return repository.loginCheck(account,password);	
	}
	//會員註冊
	public void register(CustVO custVO) {
		CustVO existId = repository.findByAccount(custVO.getCust_id());
		if (existId != null) {
			throw new RuntimeException("該帳號已被註冊，請換一個帳號");
		}
		repository.save(custVO); //custVO沒有ID傳進來時，會自動INSERT;custVO有ID時，會自動寫UPDATE
	}
	//會員資料修改
	public void updateProfile(CustVO custVO) {
		System.out.println("更新的會員ID：" + custVO.getCust_id());
		System.out.println("更新的會員名字：" + custVO.getCust_name());
		repository.save(custVO); //custVO沒有ID傳進來時，會自動INSERT;custVO有ID時，會自動寫UPDATE
		}
	
	
}
