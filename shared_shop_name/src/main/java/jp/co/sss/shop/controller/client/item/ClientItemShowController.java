package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
//import jp.co.sss.shop.service.StockCalc;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;
	/**

	 * カテゴリ情報
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 商品在庫数計算サービス
	 */
	//	@Autowired
	//	StockCalc stockCalc;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		return "index";
	}

	/**
	 * 商品一覧表示（カテゴリ検索）
	 *
	 * @param categoryId カテゴリID
	 * @param model Viewとの値受渡し
	 * @return 商品一覧画面
	 */
	/*
	 * @RequestMapping(path = "/client/item/list/1", method = { RequestMethod.GET,
	 * RequestMethod.POST }) public String showListItems(
	 * 
	 * @RequestParam(required = false) Integer categoryId, Model model) {
	 * 
	 * List<Item> items;
	 * 
	 * // 全商品 if (categoryId == null || categoryId == 0) {
	 * 
	 * items = itemRepository.findAllByDeleteFlagOrderByInsertDateDesc(
	 * Constant.NOT_DELETED);
	 * 
	 * } else {
	 * 
	 * // カテゴリ検索 items =
	 * itemRepository.findAllByDeleteFlagAndCategoryIdOrderByInsertDateDesc(
	 * Constant.NOT_DELETED, categoryId); }
	 * 
	 * model.addAttribute("items", items);
	 * 
	 * model.addAttribute("categories",
	 * categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(
	 * Constant.NOT_DELETED));
	 * 
	 * model.addAttribute("categoryId", categoryId);
	 * 
	 * return "client/item/list"; }
	 */

	/**
	 * 商品詳細表示
	 *
	 * @param model 商品情報を渡すため
	 * @param id 商品ID
	 * @return 商品詳細画面
	 */
	@RequestMapping(path = "/client/item/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String details(Model model, @PathVariable Integer id) {

		Item detailItem = itemRepository.findById(id).orElse(null);

		// 商品在庫数を設定
		//			stockCalc.updateOneItemStock(detailItem);

		model.addAttribute("item", detailItem);

		return "client/item/detail";
	}

	/**
	 * 商品検索
	 *
	 * @param searchItems 検索文字
	 * @param model Viewとの値受渡し
	 * @return 商品一覧画面
	 */
	@RequestMapping(path = "/client/item/list/search/")
	public String topSearch(
			@RequestParam String searchItems,
			Model model) {

		List<Item> items = itemRepository.findAllByNameContainingAndDeleteFlagOrderByInsertDateDesc(
		        searchItems,
		        Constant.NOT_DELETED);

		//		            stockCalc.updateManyItemStock(items);

		model.addAttribute("searchItems", searchItems);

		model.addAttribute("items", items);

		model.addAttribute("categories",
				categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(
						Constant.NOT_DELETED));

		return "client/item/list";
	}

	
	@RequestMapping(path = "/client/item/list/1", method = { RequestMethod.GET, RequestMethod.POST })
	public String showAll( Model model) {
		List<Item> items = itemRepository.findAll();
		model.addAttribute("items", items);
		return "client/item/list";
	}
	 
	}
