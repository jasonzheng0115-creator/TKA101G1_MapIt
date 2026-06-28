package com.ticket.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.prod.model.ProdService;
import com.prod.model.ProdVO;

@Service
public class TicketService {
	
	@Autowired
	private TicketRepository repository;
	@Autowired
	@Lazy //解決prodService的循環依賴問題
	private ProdService prodService;

	
	//     回傳值          名稱                輸入條件參數
	public List<TicketVO> getTicketsByProdId(Integer prodId){
		return repository.findByProductId(prodId);
	}

	public List<TicketVO> getAllTickets(){
		return repository.findAll();
	}
	
	//生成新票券  名稱       輸入條件參數
	public void addTickets(ProdVO prodVO, Integer amount) {
		for(int i=0;i<amount;i++) {
			TicketVO newTicketVO = new TicketVO();
			newTicketVO.setProductVO(prodVO);
			newTicketVO.setTktSale(0);
			repository.save(newTicketVO);
			
		}
	}
	
	//作廢票券    名稱
	public void cancelTickets(Integer productId, Integer amount){
		List<TicketVO> unsoldTickets = repository.findByProdIdAndTktSale(productId, 0);
		if(unsoldTickets.size()<amount) {
			throw new RuntimeException("庫存低於未售出的票券數量，無法作廢！");
		}else {
			for(int j=0;j<amount;j++) {
				TicketVO ticket = unsoldTickets.get(j);
				ticket.setTktSale(2);
				repository.save(ticket);
			}
		}
	}
	
	//判斷要生成還是作廢票券
	public void addOrCancelTicket(ProdVO oldProd, ProdVO newProd) {
		int oldQty = 0;
		//檢查確實有這個商品存在
		if(oldProd != null) {
			oldQty = oldProd.getProductQty();
		}
		//跑完if剩下的確實是沒有建立過票券的新商品
		int newQty = newProd.getProductQty();
		//變動後差異的數量
		int changeAmount = newQty-oldQty;
		//判斷是要生成還是要作廢票券
		if(changeAmount>0) {
			addTickets(newProd,changeAmount);
		}else if(changeAmount<0){ 
			int cancelAmount = changeAmount * -1; //負數換成正數
			cancelTickets(newProd.getProductId(),cancelAmount);
		}
	}
	
	//票券總和清單的DTO
	public List<TicketTotalDTO> getTicketTotal(Integer searchProdId, String searchProdName) {
		//給一個空的大箱子，規格是TicketTotalDTO
		List<TicketTotalDTO> list= new ArrayList<>();
		//用跟prodService.getAll()，同樣型態的去拿
		List<ProdVO> prodGetAll = prodService.getAll();
		//把	ProdVO的資料一個個拿出來檢查 for(元素型別 變數名稱 : 陣列或集合)
		for(ProdVO getOneProdVO: prodGetAll) {
			//票券中的查詢功能
			if(searchProdId != null && !getOneProdVO.getProductId().equals(searchProdId)) {
				continue;													  //本次拿取的單個商品名稱『沒有包含』使用者在搜尋列給的字嗎？
			}if(searchProdName != null && !searchProdName.trim().isEmpty() && !getOneProdVO.getProductName().contains(searchProdName)) {
				continue;
			}
			
			//每一個資料都要有一個新的DTO箱子
			TicketTotalDTO smallList = new TicketTotalDTO();
			smallList.setProductId(getOneProdVO.getProductId());
			smallList.setProductName(getOneProdVO.getProductName());
			//呼叫repository，依照本次迴圈的商品id，未售出票券有幾張(.size())
			int unsoldCount = repository.findByProdIdAndTktSale(getOneProdVO.getProductId(),0).size();
			int soldCount = repository.findByProdIdAndTktSale(getOneProdVO.getProductId(),1).size();
			int cancelCount = repository.findByProdIdAndTktSale(getOneProdVO.getProductId(),2).size();
			//把銷售狀態不同的票券加總，放進smallList指定要TicketTotalDTO箱子的小格子裡
			smallList.setUnsoldCount(unsoldCount);
			smallList.setSoldCount(soldCount);
			smallList.setCancelCount(cancelCount);
			//每一筆資料，都已經裝好想要的資料，存進去小箱子裡
			list.add(smallList);
		}
		//把ProdVO每一筆都加工成為DTO，再把所有處理好的放進大箱子裡
		return list;
	}
	
}
