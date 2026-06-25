package com.ticket.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prod.model.ProdVO;

@Service
public class TicketService {
	
	@Autowired
	private TicketRepository repository;

	
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
	
	
//	拿出指定商品，未售出的票券總數
//	
//	生成指定商品，指定數量的，未售出票券
//	
//	作廢指定商品，指定數量的，未售出票券
//	如果作廢的指定商品低於未售出票券，便跳出提示不能刪除
//	
//	判斷是要生成還是作廢
	
}
