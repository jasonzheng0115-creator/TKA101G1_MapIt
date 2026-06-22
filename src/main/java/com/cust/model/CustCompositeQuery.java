package com.cust.model;

import java.util.*;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CustCompositeQuery {
	
	//【條件過濾用】if/else判斷要查什麼，並組裝對應的SQL條件
	//CriteriaBuilder builder:呼叫功能,
	//Root<CustVO> root:裝資料庫的值和前端的值,
	//String columnName:使用者填寫資料的欄位表頭,
	//String value:使用者填寫的資料
	public static Predicate getPredicate(CriteriaBuilder builder, Root<CustVO> root, String columnName, String value) {
		
		Predicate predicate = null;
		
		if("cust_account".equals(columnName) || "cust_name".equals(columnName))
		    // 呼叫builder,把root裝的資料(root.get(columnName))拿出來,模糊比對(like),回傳(predicate)
			predicate = builder.like(root.get(columnName), "%"+value+"%");
		else if("cust_right".equals(columnName))
			predicate = builder.equal(root.get(columnName), String.valueOf(value));
		else if("cust_use".equals(columnName))
			predicate = builder.equal(root.get(columnName),String.valueOf(value));
		
		
		
		return predicate;
	}
	
	//【查詢執行用】把前端傳來的條件用map解開，叫上面的過濾器安裝
	//Spring Boot版：改用EntityManager，不用手動開session關閉連線
	public static List<CustVO> getAllC(Map<String,String[]> map, EntityManager em){
		
		List<CustVO> list = null;
		
		//呼叫builder，讓它判斷是什麼欄位表頭(session)，要equal還是like
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//提供空白的清單，讓客製化條件存在這
		CriteriaQuery<CustVO> criteriaQuery = builder.createQuery(CustVO.class);
		//對應資料表，告訴builder是要確認哪一個欄位的
		Root<CustVO> root = criteriaQuery.from(CustVO.class);
		
		//拆解前端丟過來的資料，準備空陣列
		List<Predicate> predicateList = new ArrayList<Predicate>();
		//拿到自訂的所有標籤清單
		Set<String> keys = map.keySet();
		int count = 0;
		//把標籤一個一個拿出來看
		for(String key :keys) {
			String value = map.get(key)[0];
			//!"action".equals(key)：有些表單會傳送action隱藏按鈕值，跟資料庫欄位無關，直接忽略
			if(value != null && value.trim().length()!= 0 && !"action".equals(key)){
				count++;
				//通過篩選的乾淨資料
				predicateList.add(getPredicate(builder,root,key,value.trim()));
			}
		}
		
		//不接受List，只接受Array，所以使用.toArray(...)，變成標準長度的陣列，放進where條件
		criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
		criteriaQuery.orderBy(builder.asc(root.get("cust_id")));
		
		Query query =em.createQuery(criteriaQuery);
		list = query.getResultList();
		
		
		return list;
	}
}
