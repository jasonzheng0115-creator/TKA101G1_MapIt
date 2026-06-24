package com.orders.model;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prod.model.ProdRepository;
import com.prod.model.ProdVO;

@Service
public class OrdersService {
	
	@Autowired
	private OrdersRepository ordersRepository;
	
	@Autowired
	private ProdRepository prodRepository;
	
	// 結帳是一個「一連串」的動作：存訂單 ➔ 存明細 ➔ 扣商品 A 庫存 ➔ 扣商品 B 庫存。
	// 安全結帳邏輯（創建訂單 ＋ 扣減庫存）
	// @Transactional
	// 只要這個方法內部的任何一行程式碼出錯（例如庫存不足噴異常），
	// Spring 會自動啟動「原子性回滾 (Rollback)」，把已經扣掉的庫存、塞進去的訂單全部撤銷！
	// 絕對不會發生「庫存扣了，訂單卻沒成立」或「錢收了卻沒收到單」的電商災難！
	@Transactional
	public OrdersVO insertOrder(OrdersVO ordersVO) {
		
		// 防禦性檢查：確認這張訂單裡面有沒有明細
		if (ordersVO.getOrderItems() == null || ordersVO.getOrderItems().isEmpty()) {
			throw new IllegalArgumentException("結帳失敗：訂單內必須包含至少一項商品項目！");
		}
		
		// 走訪每一筆明細，進行「庫存安全校驗與扣減」
		for (OrderItemVO item : ordersVO.getOrderItems()) {
			// A. 透過商品 Repository，拿著明細裡的 productId 去資料庫把該商品的最新狀態揪出來
			ProdVO prod = prodRepository.findById(item.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("結帳失敗：找不到商品編號 " + item.getProductId() + " 的商品！"));
			
			// B. 抓取該商品目前的庫存量與消費者想購買的數量
			// 這裡的 getProdStock() 對齊商品 ProdVO 裡面的「庫存變數名稱」
			int currentStock = prod.getProductQty();
			int buyQty = item.getItemQty();
			
			// C. 如果庫存不夠，直接噴出執行期異常（Runtime Exception）
			// 因為有 @Transactional，這裡一噴異常，前面縱使扣了其他商品的庫存，也會全數回滾撤銷！
			if (currentStock < buyQty) {
				throw new IllegalStateException("結帳失敗：商品【" + prod.getProductName() + "】庫存不足！目前剩餘庫存：" + currentStock + "，您欲購買：" + buyQty);
			}
			
			// D. 安全價格防禦：強迫以資料庫真實價格為主，防止前端被惡意篡改金額
			item.setItemPrice(prod.getProductPrice());
			
			// E. 💥 扣庫存與累加累積銷量
			prod.setProductQty(currentStock - buyQty); // 扣庫存
			
			// 如果原本銷量是 null 就設為 0，接著把這次買的數量加上去！
			int currentPurchased = (prod.getPurchasedQty() == null) ? 0 : prod.getPurchasedQty();
			prod.setPurchasedQty(currentPurchased + buyQty);
			
			// F. 將最新庫存同步回存到商品表格（MySQL）中
			prodRepository.save(prod);
			
			// G. 強迫將明細與目前這張訂單綁在一起（讓 JPA 認得雙向關聯）
			item.setOrdersVO(ordersVO);
		}
		
		// 最後收尾：存檔
		// 因為我們在 OrdersVO 身上設定了 cascade = CascadeType.ALL
		// 這裡只要 save(ordersVO) ，JPA 就會自動幫我們：
		// 1. 在 ORDERS 表格 insert 一筆訂單主檔。
		// 2. 拿到最新自增的 ORDER_ID 之後，自動塞進明細裡。
		// 3. 在 ORDER_ITEM 表格自動 insert 所有的明細
		return ordersRepository.save(ordersVO);
	}
	
	// 逆向取消訂單邏輯（更新狀態 ＋ 退還庫存 ＋ 扣減銷量）
	@Transactional
	public OrdersVO cancelOrder(Integer orderId, String cancelReason) {
		
		// 1. 尋找目標：用orderId 去資料庫把這張訂單撈出來
		OrdersVO order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("取消失敗：找不到訂單編號 " + orderId + " 的訂單！"));
	
		// 2. 如果訂單已經是取消狀態，直接攔截，防止重複退庫存造成的「庫存無限膨脹地雷」
		if ("已取消".equals(order.getOrderStatus())) {
			throw new IllegalStateException("取消失敗：該訂單先前已執行過取消，請勿重複操作！");
		}
		
		// 3. 走訪這張訂單底下的所有明細，把庫存與銷量「物歸原主」
		for (OrderItemVO item : order.getOrderItems()) {
			
			// A. 揪出對應的商品
			ProdVO prod = prodRepository.findById(item.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("取消失敗：找不到明細中商品編號 " + item.getProductId() + " 的商品！"));
			
			// B. 拿回目前的庫存與銷量
			int currentStock = prod.getProductQty();
			int currentPurchased = (prod.getPurchasedQty() == null) ? 0 : prod.getPurchasedQty();
			int refundQty = item.getItemQty(); // 當初購買的數量（即今天要退還的數量）
		
			// C. 庫存加回去，累積銷量扣掉
			prod.setProductQty(currentStock + refundQty);
			
			// 銷量扣除後如果小於 0，強迫歸零，防止出現負數銷量
			int newPurchased = currentPurchased - refundQty;
			prod.setPurchasedQty(Math.max(0, newPurchased));
			
			// D. 同步回存商品表
			prodRepository.save(prod);
		}
		
		// 4. 狀態更新
		order.setOrderStatus("已取消");
		order.setOrderCancel(cancelReason); // 寫入取消原因（例如："不小心買錯了"、"客服協調退單"）
		order.setCancelDate(LocalDateTime.now()); // 寫入最精準的退單當下時間
	
		// 5. 更新存檔並回傳
		return ordersRepository.save(order);
	}
}
