package jp.co.sss.shop.controller.admin.item;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(運用管理者用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;
	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 一覧データ取得、一覧表示　処理
	 *
	 * @param model Viewとの値受渡し
	 * @param pageable ページ制御用
	 * @return "admin/category/list" 一覧画面 表示
	 */
	@RequestMapping(path = "/admin/item/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showItemList(Model model, Pageable pageable) {

		// 商品情報の登録数の取得と新規追加可否チェック
		Long itemsCount = itemRepository.count();
		Boolean registrable = true;
		if (itemsCount == Constant.ITEMS_MAX_COUNT) {
			//商品情報の登録数が最大値の場合、新規追加不可
			registrable = false;
		}

		// 商品情報を全件検索(新着順)
		//表示画面でページングが必要なため、ページ情報付きの検索を行う
		Page<Item> itemsPage = itemRepository.findByDeleteFlagOrderByInsertDateDescPage(Constant.NOT_DELETED, pageable);

		// エンティティ内のページ情報付きの検索結果からレコードの情報だけをJavaBeansに保存
		List<Item> itemList = itemsPage.getContent();

		// 商品情報をViewへ渡す
		model.addAttribute("registrable", registrable);
		model.addAttribute("pages", itemsPage);
		model.addAttribute("items", itemList);

		//商品登録・変更・削除用のセッションスコープを初期化
		session.removeAttribute("itemForm");

		return "admin/item/list";
	}

	/**
	 * 商品情報詳細表示処理
	 *
	 * @param id  商品ID
	 * @param model  Viewとの値受渡し
	 * @return "admin/item/detail" 詳細画面 表示
	 * 
	 * TIPS: 一般会員向けの商品詳細表示機能に類似した処理です。
	 */
	@RequestMapping(path = "/admin/item/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showItem(@PathVariable int id, Model model) {

		// 対象の商品情報を取得
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

		if (item == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		//Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);
		//商品登録・変更・削除用のセッションスコープを初期化
		session.removeAttribute("itemForm");

		return "admin/item/detail";
	}
}
