package com.ticket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.cust.model.CustVO;
import com.orders.model.OrderItemVO;
import com.orders.model.OrdersVO;
import com.prod.model.ProdService;
import com.prod.model.ProdVO;

@Service
public class TicketService {
	
	@Autowired
	private TicketRepository repository;
	@Autowired
	private TicketItemRepository ticketItemRepository;
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
	
	//照商品編號和訂單數量，把指定數量的票卷的銷售狀態，從未售出票券改成已售出
	public List<TicketVO> sendTicketByProdId(Integer prodId, Integer limit){
		List<TicketVO> unsoldTickets = repository.findUnsoldTickets(prodId, limit);
		if(unsoldTickets.size()<limit) {
			throw new RuntimeException("票券庫存不足");
		}
		for(int i=0;i<limit;i++) {
			TicketVO ticket = unsoldTickets.get(i);
			ticket.setTktSale(1);
			repository.save(ticket);
		}
		return unsoldTickets;
	}
	
	// 結帳成功後，系統自動配發實體票券明細給會員
	public void ticketForOrder(OrdersVO order) {
		//從訂單中拿到買家
		CustVO cust = order.getCustVO(); 
		//拿出訂單下每一筆訂單明細 for(元素型別 變數名稱 : 陣列或集合)
		for(OrderItemVO item : order.getOrderItems()) {
			//呼叫上方的方法，將票券改為已售出
			List<TicketVO> tickets = sendTicketByProdId(item.getProductId(), item.getItemQty());
			
			for(TicketVO ticket : tickets) {
				TicketItemVO ticketItemVO = new TicketItemVO();
				ticketItemVO.setTicketId(ticket);
				ticketItemVO.setCustId(cust);
				ticketItemVO.setTicketStatus("未使用");
				ticketItemVO.setStartDate(order.getOrderTimestamp());
				
				int useTime = ticket.getProductVO().getUsePeriod();
				LocalDateTime endTime = order.getOrderTimestamp().plusMonths(useTime);
			    endTime = endTime.withHour(23).withMinute(59).withSecond(59);
			    ticketItemVO.setEndDate(endTime);
			    
				ticketItemRepository.save(ticketItemVO);
			}
			
		}
	}
	
	// 店家透過輸入 PIN 碼來核銷票券
	public boolean exchangeTicket(Integer tktId, String pinCode) {
		//寫死的店家密碼
		String correctPin = "8888";
		//檢查密碼對不對
		if (!correctPin.equals(pinCode)) {
			return false; // 密碼錯誤，回傳失敗
		}
		//從資料庫拿到可能裝有票，也可能為空的盒子 (Optional)
		Optional<TicketVO> ticketBox = repository.findById(tktId);
		//準備一個變數來裝票
		TicketVO ticket;
		//用 if-else 來檢查盒子裡面有沒有東西
		if (ticketBox.isPresent()) {
		    // 如果有找到(isPresent = 存在)，就把票從盒子裡拿出來(get)
		    ticket = ticketBox.get(); 
		} else {
		    // 如果沒找到，就給null
		    ticket = null; 
		}
		//再找這張票對應的使用者明細TicketItemVO
		TicketItemVO ticketItem = ticketItemRepository.findByTktId(tktId);
		
		if (ticket != null && ticketItem != null) {
			// 將票券狀態改為2(已作廢/已使用)
			ticket.setTktSale(2);
			// 將使用者的票券明細狀態改為 "已使用"
			ticketItem.setTicketStatus("已使用");
			// 存回資料庫
			repository.save(ticket);
			ticketItemRepository.save(ticketItem);
			return true; // 成功核銷
		}
		return false; // 找不到票券
	}
	
}
