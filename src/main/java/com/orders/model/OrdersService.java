package com.orders.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.message.model.MessageService;
import com.prod.model.ProdRepository;
import com.prod.model.ProdVO;
import com.ticket.model.TicketService;

@Service
public class OrdersService {
	
	@Autowired
	private OrdersRepository ordersRepository;
	
	@Autowired
	private ProdRepository prodRepository;
	
	// ========= 導入票券、通知Service =========
	@Autowired
	private TicketService ticketService;
	@Autowired
	private MessageService messageService;
	// ========================================
	
	// 結帳是一個「一連串」的動作：存訂單 ➔ 存明細 ➔ 扣商品 A 庫存 ➔ 扣商品 B 庫存。
	// 安全結帳邏輯（創建訂單 ＋ 扣減庫存）
	// @Transactional
	// 只要這個方法內部的任何一行程式碼出錯（例如庫存不足噴異常），
	// Spring 會自動啟動「原子性回滾 (Rollback)」，把已經扣掉的庫存、塞進去的訂單全部撤銷！
	// 絕對不會發生「庫存扣了，訂單卻沒成立」或「錢收了卻沒收到單」的電商災難！
	@Transactional
	public OrdersVO insertOrder(OrdersVO ordersVO) {
		
		// 在開始任何計算與存檔之前，先把下單時間設為現在，前台結帳畫面才會成立
	    ordersVO.setOrderTimestamp(LocalDateTime.now());
	    
		// 防禦性檢查：確認這張訂單裡面有沒有明細
		if (ordersVO.getOrderItems() == null || ordersVO.getOrderItems().isEmpty()) {
			throw new IllegalArgumentException("結帳失敗：訂單內必須包含至少一項商品項目！");
		}
		
		// 將新訂單的狀態初始化為「已結單」,新訂單產生表示完成付款
	    ordersVO.setOrderStatus("已結單");
		
		// 走訪每一筆明細，進行「庫存安全校驗與扣減」
		for (OrderItemVO item : ordersVO.getOrderItems()) {
			// 透過商品 Repository，拿著明細裡的 productId 去資料庫取出該商品的最新狀態
			ProdVO prod = prodRepository.findById(item.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("結帳失敗：找不到商品編號 " + item.getProductId() + " 的商品！"));
			
			// 抓取該商品目前的庫存量與消費者想購買的數量
			// 這裡的 getProdStock() 對齊商品 ProdVO 裡面的「庫存變數名稱」
			int currentStock = prod.getProductQty();
			int buyQty = item.getItemQty();
			
			// 如果庫存不夠，直接噴出執行期異常（Runtime Exception）
			// 因為有 @Transactional，這裡一噴異常，前面縱使扣了其他商品的庫存，也會全數回滾撤銷
			if (currentStock < buyQty) {
				throw new IllegalStateException("結帳失敗：商品【" + prod.getProductName() + "】庫存不足！目前剩餘庫存：" + currentStock + "，您欲購買：" + buyQty);
			}
			
			// 安全價格防禦：強迫以資料庫真實價格為主，防止前端被惡意篡改金額
			item.setItemPrice(prod.getProductPrice());
			
			// 扣庫存
			//	prod.setProductQty(currentStock - buyQty); 
			// 後來ticket扣了庫存,因此這裡不需要,否則會重複扣除
			
			// 如果原本銷量是 null 就設為 0，接著把這次買的數量加上去
			int currentPurchased = (prod.getPurchasedQty() == null) ? 0 : prod.getPurchasedQty();
			prod.setPurchasedQty(currentPurchased + buyQty);
			
			// 將最新庫存同步回存到商品表格（MySQL）中
			prodRepository.save(prod);
		}
		
		// 分兩次存檔
		// 先把前端傳過來的明細清單暫時拿出來，並把主訂單內部的清單清空
		// 這樣做是為了欺騙 JPA，叫它先不要急著在第一時間級聯存明細
		java.util.List<OrderItemVO> tempItems = ordersVO.getOrderItems();
		ordersVO.setOrderItems(new java.util.ArrayList<>());
		
		// 主訂單單獨先行存檔 此時資料庫會發配最新的自動遞增 ID 
		OrdersVO savedOrder = ordersRepository.save(ordersVO);
		
		// 拿到實體 ID 之後，再跑迴圈幫明細放回去、並把數字補上
		for (OrderItemVO item : tempItems) {
			item.setOrdersVO(savedOrder);                  // 綁定主檔物件
			item.setOrderId(savedOrder.getOrderId());      // 手動補入剛產生的訂單純數字 ID
			
			// 把明細塞回剛剛存完的主檔清單中，維持物件結構完整
			savedOrder.getOrderItems().add(item);
		}
		
		// 讓主檔完整的明細再次回存
		//return ordersRepository.save(savedOrder);
		// ===== 先存進存檔ordersRepository，再把訂單的資料傳給ticketService =====
		OrdersVO finalOrder = ordersRepository.save(savedOrder);
		ticketService.ticketForOrder(finalOrder);
		// ======通知訂單完成跟票券發送通知給會員==================================		
		//抓出訂單是哪格會員買的
		Integer custId = finalOrder.getCustVO().getCustId();
		messageService.sendNotificationToUser(
		custId,"🛒 結帳成功通知", "您的訂單 (編號: " + finalOrder.getOrderId() + ") 已成功結帳！感謝您的購買。", 
	    null // 如果沒有圖片就傳 null
		);
		messageService.sendNotificationToUser(
		custId,"🎟️ 票券發放通知", "您購買的票券已經全數發放至「我的票券匣」，趕快去看看吧！", 
		null // 如果沒有圖片就傳 null
		);
		//===================================================================
		return finalOrder;
	}
	
	// 取消訂單 逆向邏輯（更新狀態 ＋ 退還庫存 ＋ 扣減銷量）
	@Transactional
	public OrdersVO cancelOrder(Integer orderId, String cancelReason) {
		
		// 用orderId 去資料庫把這張訂單撈出來
		OrdersVO order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("取消失敗：找不到訂單編號 " + orderId + " 的訂單！"));
	
		// 如果訂單已經是取消狀態，直接攔截，防止重複退庫存造成的「庫存無限膨脹地雷」
		if ("已取消".equals(order.getOrderStatus())) {
			throw new IllegalStateException("取消失敗：該訂單先前已執行過取消，請勿重複操作！");
		}
		
		// 呼叫票券回收與作廢邏輯（如果內部拋出異常，整筆訂單取消就會取消並回滾）
		ticketService.cancelTicketsForOrder(order);
		
		// 走訪這張訂單底下的所有明細，把庫存與銷量放回去
		for (OrderItemVO item : order.getOrderItems()) {
			
			// 揪出對應的商品
			ProdVO prod = prodRepository.findById(item.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("取消失敗：找不到明細中商品編號 " + item.getProductId() + " 的商品！"));
			
			// 拿回目前的庫存與銷量
			int currentStock = prod.getProductQty();
			int currentPurchased = (prod.getPurchasedQty() == null) ? 0 : prod.getPurchasedQty();
			int refundQty = item.getItemQty(); // 當初購買的數量（即今天要退還的數量）
		
			// 庫存加回去，累積銷量扣掉
			// prod.setProductQty(currentStock + refundQty);
			// 後來ticket存回了庫存,因此這裡不需要,否則會重複加
			
			// 銷量扣除後如果小於 0，強迫歸零，防止出現負數銷量
			int newPurchased = currentPurchased - refundQty;
			prod.setPurchasedQty(Math.max(0, newPurchased));
			
			// 同步回存商品表
			prodRepository.save(prod);
		}
		
		// 狀態更新
		order.setOrderStatus("已取消");
		order.setOrderCancel(cancelReason); // 寫入取消原因（例如："不小心買錯了"、"客服協調退單"）
		order.setCancelDate(LocalDateTime.now()); // 寫入最精準的退單當下時間
	
		// 更新存檔並回傳
		return ordersRepository.save(order);
	}
	
	// 用 orderId 撈單筆訂單(包含明細)
	public OrdersVO getOneOrders(Integer orderId) {
		return ordersRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("找不到訂單編號：\" + orderId + \" 的資料！"));
	}
	
	// 用客戶編號 (custId) 撈歷史訂單
	public List<OrdersVO> getOrdersByCustId(Integer custId) {
		return ordersRepository.findByCustVO_CustIdOrderByOrderTimestampDesc(custId);
	}
}
