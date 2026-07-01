package jp.co.sss.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.CategoryBean;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.form.ItemForm;

/**
 * オブジェクト間でのフィールドコピー処理を行うクラス
 *
 * @author System Shared
 */
@Service
public class BeanTools {
	/**
	 * ItemFormクラスの各フィールドの値をItemBeanクラスにコピー
	 *
	 * @param form コピー元のオブジェクト
	 * @return コピー先のオブジェクト
	 */
	public ItemBean copyItemFormToItemBean(ItemForm form) {

		ItemBean bean = new ItemBean();

		// 商品フォームに入力された情報を商品情報にコピー
		BeanUtils.copyProperties(form, bean);

		bean.setId(form.getId());

		//		bean.setPrice(Integer.parseInt(form.getPrice()));

		bean.setCategoryId(form.getCategoryId());

		//		bean.setStock(Integer.parseInt(form.getStock()));

		return bean;
	}

	/**
	 * ItemFormクラスの各フィールドの値をItemエンティティにコピー
	 *
	 * @param form コピー元のオブジェクト
	 * @return コピー先のエンティティ
	 */
	public Item copyItemFormToEntity(ItemForm form) {
		Category category = new Category();
		Item entity = new Item();

		BeanUtils.copyProperties(form, entity);

		if (form.getId() != null) {
			entity.setId(form.getId());
		}

		//		if (form.getPrice() != null && form.getPrice().length() > 0) {
		//			entity.setPrice(Integer.parseInt(form.getPrice()));
		//		}
		//
		//		if (form.getStock() != null && form.getStock().length() > 0) {
		//			entity.setStock(Integer.parseInt(form.getStock()));
		//		}

		category.setId(form.getCategoryId());
		entity.setCategory(category);

		return entity;
	}

	/**
	 * Itemエンティティの各フィールドの値をItemBeanクラスにコピー
	 *
	 * @param entity  コピー元のエンティティ
	 * @return コピー先のオブジェクト
	 */
	public ItemBean copyEntityToItemBean(Item entity) {
		ItemBean bean = new ItemBean();

		BeanUtils.copyProperties(entity, bean);

		bean.setCategoryId(entity.getCategory().getId());
		bean.setCategoryName(entity.getCategory().getName());

		return bean;
	}

	/**
	 * Orderエンティティの各フィールドの値をOrderBeanクラスにコピー
	 *
	 * @param entity  コピー元のエンティティ
	 * @return コピー先のオブジェクト
	 */
	public OrderBean copyEntityToOrderBean(Order entity) {
		OrderBean bean = new OrderBean();

		BeanUtils.copyProperties(entity, bean);

		bean.setInsertDate(entity.getInsertDate().toString());

		// 会員名を注文情報に設定
		bean.setUserName(entity.getUser().getName());

		return bean;
	}

	/**
	 * Itemエンティティの各フィールドの値をItemFormクラスにコピー
	 *
	 * @param entity コピー元のエンティティ
	 * @return コピー先のオブジェクト
	 */
	public ItemForm copyEntityToItemForm(Item entity) {
		ItemForm form = new ItemForm();

		BeanUtils.copyProperties(entity, form);

		form.setCategoryId(entity.getCategory().getId());
		form.setCategoryName(entity.getCategory().getName());
		//	form.setPrice(entity.getPrice());
		//	form.setStock(entity.getStock());

		return form;
	}

	/**
	 * Itemエンティティの各フィールドの値をItemBeanクラスにコピー(リスト形式)
	 *
	 * @param entityList コピー元のエンティティ(リスト形式)
	 * @return コピー先のオブジェクト(リスト形式)
	 */
	public List<ItemBean> copyEntityListToItemBeanList(List<Item> entityList) {
		List<ItemBean> beanList = new ArrayList<ItemBean>();

		for (Item entity : entityList) {
			ItemBean bean = new ItemBean();
			BeanUtils.copyProperties(entity, bean);

			if (entity.getCategory() != null) {
				bean.setCategoryName(entity.getCategory().getName());
			}

			beanList.add(bean);
		}

		return beanList;
	}

	/**
	 * Categoryエンティティの各フィールドの値をCategoryBeanクラスにコピー(リスト形式)
	 *
	 * @param entityList コピー元のエンティティ(リスト形式)
	 * @return コピー先のオブジェクト(リスト形式)
	 */
	public List<CategoryBean> copyEntityListToCategoryBeanList(List<Category> entityList) {
		List<CategoryBean> beanList = new ArrayList<CategoryBean>();

		for (Category entity : entityList) {
			CategoryBean bean = new CategoryBean();
			BeanUtils.copyProperties(entity, bean);
			beanList.add(bean);
		}

		return beanList;
	}

	/**
	 * 商品情報と買い物かご商品情報から、注文商品情報を生成する
	 * @param item 商品情報
	 * @param basketBean 買い物かご商品情報
	 * @return　注文商品情報
	 */
	public OrderItemBean generateOrderItemBean(Item item, BasketBean basketBean) {
		// オーダー商品情報の作成とリスト追加
		OrderItemBean orderItemBean = new OrderItemBean();
		BeanUtils.copyProperties(item, orderItemBean);
		orderItemBean.setOrderNum(basketBean.getOrderNum());
		int subtotal = orderItemBean.getPrice() * orderItemBean.getOrderNum();
		orderItemBean.setSubtotal(subtotal);

		return orderItemBean;

	}
	
	/**
	 * OrderItemエンティティのリストから、OrderItemBeanのリストを生成
	 * 
	 * @param orderItemList エンティティのリスト
	 * @return OrderItemBeanのリスト 小計も含む注文商品情報リスト
	 */
	public List<OrderItemBean> generateOrderItemBeanList(List<OrderItem> orderItemList) {

		// 注文商品情報を取得
		List<OrderItemBean> orderItemBeanList = new ArrayList<OrderItemBean>();
		for (OrderItem orderItem : orderItemList) {
			OrderItemBean orderItemBean = new OrderItemBean();

			orderItemBean.setName(orderItem.getItem().getName());
			orderItemBean.setPrice(orderItem.getPrice());
			orderItemBean.setOrderNum(orderItem.getQuantity());

			//購入時単価の合計値を計算
			//※OrderItemのItemフィールドからgetPriceを利用すると、購入時ではなく現在の単価になってしまう。
			int subtotal = orderItem.getPrice() * orderItem.getQuantity();

			orderItemBean.setSubtotal(subtotal);

			orderItemBeanList.add(orderItemBean);
		}
		return orderItemBeanList;
	}
}
