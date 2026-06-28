package com.ticket.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ticket.model.TicketDTO;
import com.ticket.model.TicketService;
import com.ticket.model.TicketTotalDTO;
import com.ticket.model.TicketVO;

@Controller
@RequestMapping("/ticket")
public class TicketController {

	@Autowired
	TicketService ticketservice;
	
	@Autowired //自動注入，用來拿商品清單
	com.prod.model.ProdService prodSvc;
	
	//票券詳細功能
    @GetMapping("/ticketList")
    public String ticketList(ModelMap model) {
        // 去資料庫把所有商品撈出來，放在名為prodList的托盤上，交給前端 HTML
        model.addAttribute("prodList", prodSvc.getAll());
        return "back-end/ticket/ticketList"; 
    }
	
	@GetMapping("api/ticketList")
	@ResponseBody //轉換單一個為JSON，要注意VO不可以用lazy，用了要加@JsonIgnoreProperties忽略
	public List<TicketDTO> TicketJson( //因為原本的資料太多，所以改用自訂的Json資料(DTO)
		@RequestParam(value="prodId",required=false) Integer prodId) {
		//跟資料庫拿TicketVO
		List<TicketVO> ticketListAll;
			if(prodId != null) {
				ticketListAll = ticketservice.getTicketsByProdId(prodId);
			}else {
				ticketListAll = ticketservice.getAllTickets();
			}
		//準備空的DTO陣列
		List<TicketDTO> resultList = new ArrayList<>();
		//開始抄寫進去DTO for(元素類型 變數名稱 : 陣列或集合名稱)
		for(TicketVO vo : ticketListAll) {
			TicketDTO dto = new TicketDTO();
			//抄寫表格本身的資料
			dto.setTktId(vo.getTktId());
			//抄寫關聯其他人的資料，要連續打開
			if(vo.getProductVO() != null) {
				vo.getProductVO(); //從票券(vo)身上，拿出商品紙箱
				vo.getProductVO().getProductName(); //打開商品紙箱，拿出商品名稱
				dto.setProductName(vo.getProductVO().getProductName()); //把拿到的名稱，寫到dto上
			}else {
				dto.setProductName("未知商品");	
			}
			
			if(vo.getTktSale()==1) {
				dto.setSaleStatus("已售出");
			}else if(vo.getTktSale()==0) {
				dto.setSaleStatus("未售出");
			}else {
				dto.setSaleStatus("已作廢");
			}
			//跑迴圈後乾淨的參數，一個個加進dto變數
			resultList.add(dto);
		}
		//回傳乾淨的結果給fetch(url)
			return resultList;
		}
	
	//票券統整功能
	@GetMapping("api/ticketAll")
	@ResponseBody //轉換單一個為JSON
	public List<TicketTotalDTO> TicketTotalJSON(
		@RequestParam(value="prodId",required=false) Integer prodId,
		@RequestParam(value="prodName",required=false) String prodName
		){
		return ticketservice.getTicketTotal(prodId,prodName);
	}
	
	
}
