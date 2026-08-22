package jp.co.sss.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.entity.OrderItem;

/**
 * 料金計算用クラス
 *
 * @author System Shared
 */
@Service
public class PriceCalc {
	/**
	 * 小計から注文した商品の合計金額を計算
	 *
	 * @param list
	 *            注文した商品情報
	 * @return 合計金額
	 */
	public int orderItemBeanPriceTotalUseSubtotal(List<OrderItemBean> list) {
		int total = 0;

		for (OrderItemBean bean : list) {
			total = total + bean.getSubtotal();
		}

		return total;
	}

	/**
	 * 単価と注文した商品の合計金額を計算
	 *
	 * @param list
	 *            注文した商品情報
	 * @return 合計金額
	 */
	public int orderItemBeanPriceTotal(List<OrderItemBean> list) {
		int total = 0;

		for (OrderItemBean orderItemBean : list) {
			total = total + (orderItemBean.getPrice() * orderItemBean.getOrderNum());
		}

		return total;
	}

	/**
	 * 注文時の単価と商品個数の合計金額を計算
	 *
	 * @param list
	 *            注文した商品情報
	 * @return 合計金額
	 */
	public int orderItemPriceTotal(List<OrderItem> list) {
		int total = 0;

		for (OrderItem orderItem : list) {
			total = total + (orderItem.getPrice() * orderItem.getQuantity() );
		}

		return total;
	}
}
