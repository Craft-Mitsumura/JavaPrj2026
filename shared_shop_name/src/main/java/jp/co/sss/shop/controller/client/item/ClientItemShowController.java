package jp.co.sss.shop.controller.client.item;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.form.ItemForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.FavoriteRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.PromotionsRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.FavoriteService;
import jp.co.sss.shop.service.ItemViewConverter;
//import jp.co.sss.shop.service.StockCalc;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared 	小松原愛
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

	@Autowired
	FavoriteRepository favoriteRepository;

	@Autowired
	HttpSession session;

	@Autowired
	FavoriteService favoriteService;

	@Autowired
	PromotionsRepository promotionsRepository;

	/**
	 * @author 金城
	 * json用のコンバーター
	 */
	@Autowired
	ItemViewConverter ItemViewConverter;

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

		// 新着商品順の表示用
		// 【変更点】PageRequest.of(0, 4) を渡して、登録日・IDの降順で確実に最新4件を取得します
		List<Item> items = itemRepository.findTop4ByDeleteFlagOrderByIdDesc(Constant.NOT_DELETED, PageRequest.of(0, 4));

		// トップ画面用に最大4件に絞り込む（元コードのロジックを維持）
		if (items.size() > 4) {
			items = items.subList(0, 4);
		}

		model.addAttribute("items", items);
		model.addAttribute("sortType", 1); // デフォルトを新着順（例として1）として扱う
		model.addAttribute(
				"categories",
				categoryRepository.findByDeleteFlagOrderByIdAsc(Constant.NOT_DELETED));

		//	    ランキング表示用
		//	    購入日をすべて1日に変更する
		LocalDate today = LocalDate.now();
		LocalDate firstDateOfMonth = today.withDayOfMonth(1);
		List<Item> findByRanking = new ArrayList<>();
		// 通常の全体用NamedQueryを呼び出す
		findByRanking = itemRepository.findItemsOrderByallRanking(firstDateOfMonth, PageRequest.of(0, 3));
		//  正しいデータが入ったリストを画面に渡す
		model.addAttribute("rankings", findByRanking);

		// カルーセル広告一覧
		List<Promotions> adList = promotionsRepository.findAllByOrderByIsActiveDescIdDesc();

		// デバッグ用
		System.out.println("広告件数：" + adList.size());

		// HTMLへ渡す
		model.addAttribute("adList", adList);

		return "index";
	}

	/**
	 * 商品詳細表示
	 * @author 金城（微修正）
	 * @param model 商品情報を渡すため
	 * @param id 商品ID
	 * @return 商品詳細画面
	 */
	@RequestMapping(path = "/client/item/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String details(Model model, @PathVariable Integer id,
			@RequestParam(required = false) Boolean isEngravingSelected,
			@RequestParam(required = false) String engravingText,
			@RequestParam(required = false) String fontType) {

		Item detailItem = itemRepository.findById(id).orElse(null);

		if (detailItem != null) {
			try {
				// メインの変換（表示用）
				ItemForm itemForm = ItemViewConverter.convertToForm(detailItem);
				model.addAttribute("item", itemForm);

				// 同じ名前の全バリエーションを取得
				List<Item> sameGroupItems = itemRepository.findSameNameItems(detailItem.getName(), Constant.NOT_DELETED);

				// Formに変換してリスト化
				List<ItemForm> variationList = new ArrayList<>();
				for (Item item : sameGroupItems) {
					variationList.add(ItemViewConverter.convertToForm(item));
				}

				// モデルに追加（これでJSから全バリエーション情報が参照可能になる）
				model.addAttribute("variationList", variationList);

				model.addAttribute("paramIsEngravingSelected", isEngravingSelected);
				model.addAttribute("paramEngravingText", engravingText);
				model.addAttribute("paramFontType", fontType);

			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("item", detailItem);
			}
		}
		UserBean loginUser = (UserBean) session.getAttribute("user");
		boolean isFavorite = false;

		if (loginUser != null) {

			Favorite favorite = favoriteRepository.findByUser_IdAndItem_IdAndDeleteFlag(
					loginUser.getId(),
					id,
					Constant.NOT_DELETED);

			isFavorite = (favorite != null);

			// 右上ハート表示用
			session.setAttribute(
					"favoriteBeans",
					favoriteService.getFavoriteList(loginUser.getId())

			);
		}

		model.addAttribute("isFavorite", isFavorite);

		return "client/item/detail";
	}

	/**
	 * 商品一覧表示（カテゴリ検索）
	 *
	 * @param id カテゴリID
	 * @param page ページ番号
	 * @param model Viewとの値受渡し
	 * @return 商品一覧画面
	 */
	@RequestMapping(path = "/client/category/lists/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String categorySearch(
			@PathVariable Integer id,
			@RequestParam(defaultValue = "0") int page,
			Model model) {

		// カテゴリ内の商品一覧（未削除のみ）をページングで取得
		Page<Item> itemPage = itemRepository.findByCategoryIdAndDeleteFlag(
				id,
				Constant.NOT_DELETED,
				PageRequest.of(page, 20)
		);

		// 追加安全策: 念のため取得後にdeleteFlagを再チェックし、削除フラグが立っている商品を除外する
		List<Item> filteredItems = new ArrayList<>();
		for (Item it : itemPage.getContent()) {
			Integer df = it.getDeleteFlag();
			if (df == null || df.intValue() == Constant.NOT_DELETED) {
				filteredItems.add(it);
			} else {
				System.out.println("Filtered out deleted item id=" + it.getId());
			}
		}
		Page<Item> filteredPage = new PageImpl<>(filteredItems, itemPage.getPageable(), filteredItems.size());
		model.addAttribute("items", filteredPage.getContent());
		model.addAttribute("page", filteredPage);

		model.addAttribute(
				"categories",
				categoryRepository.findByDeleteFlagOrderByIdAsc(Constant.NOT_DELETED));

		Category category = categoryRepository.findByIdAndDeleteFlag(id, 0);

		model.addAttribute("category", category);

		//見出しを表示する
		model.addAttribute("itemTitle", category.getName());
		model.addAttribute("cateex", category.getDescription());

		// ページ移動時にカテゴリを保持する
		model.addAttribute("categoryId", id);

		return "client/item/list";
	}

	/**
	 * 商品検索結果表示
	 *
	 * @param model Viewとの値受渡し
	 * @param items 検索する商品名
	 * @return "client/item/list" 商品一覧画面
	 */

	/**
	 * 商品一覧表示（全商品）
	 *
	 * @param model Viewとの値受渡し
	 * @param page ページ番号
	 * @return 商品一覧画面
	 */
	@RequestMapping(path = "/client/item/list/1", method = { RequestMethod.GET, RequestMethod.POST })
	public String showAll(
			Model model,
			//@PathVariable Integer sortType,
			@RequestParam(defaultValue = "0") int page) {

		//		ページの小見出しを作成する
		model.addAttribute("pageTitle", "すべての商品");
		model.addAttribute("pageDescription", "");

		Page<Item> itemPage = itemRepository.findByDeleteFlagOrderByIdDesc(
				Constant.NOT_DELETED,
				PageRequest.of(page, 20));

		model.addAttribute("items", itemPage.getContent());
		model.addAttribute("page", itemPage);

		//		見出しを表示する
		model.addAttribute("itemTitle", "すべての商品");
		model.addAttribute("cateex", "");

		model.addAttribute(
				"categories",
				categoryRepository.findByDeleteFlagOrderByIdAsc(Constant.NOT_DELETED));

		return "client/item/list";

	}

	@RequestMapping(path = "/client/item/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String keyboardSearch(@RequestParam(name = "item", required = false) String item, Model model) {
		System.out.println("triggred");
		List<Item> itemss = itemRepository.findByNameContainingIgnoreCaseAndDeleteFlag(item, Constant.NOT_DELETED);
		model.addAttribute("items", itemss);
		return "client/item/list";
	}
	
	@GetMapping (path="/client/ad/page/{id}")
	public String adPage(Model model,
			@PathVariable Integer id,
			@RequestParam(defaultValue = "0") int page
			) {
	  System.out.println("triggred");
	  // 広告ページからカテゴリ内の商品一覧（未削除のみ）をページングで取得
	  Page<Item> itemPage = itemRepository.findByCategoryIdAndDeleteFlag(
				id,
				Constant.NOT_DELETED,
				PageRequest.of(page, 20));

		// 追加安全策: 念のため取得後にdeleteFlagを再チェック
		List<Item> filteredItems2 = new ArrayList<>();
		for (Item it : itemPage.getContent()) {
			Integer df = it.getDeleteFlag();
			if (df == null || df.intValue() == Constant.NOT_DELETED) {
				filteredItems2.add(it);
			} else {
				System.out.println("Filtered out deleted item id=" + it.getId());
			}
		}
		Page<Item> filteredPage2 = new PageImpl<>(filteredItems2, itemPage.getPageable(), filteredItems2.size());
		model.addAttribute("items", filteredPage2.getContent());
		model.addAttribute("page", filteredPage2);

		model.addAttribute(
			    "categories",
			    categoryRepository.findByDeleteFlagOrderByIdAsc(Constant.NOT_DELETED)
			);

		Category category = categoryRepository.findByIdAndDeleteFlag(id, 0);

		model.addAttribute("category", category);

		//見出しを表示する
		model.addAttribute("itemTitle", category.getName());
		model.addAttribute("cateex", category.getDescription());

		// ページ移動時にカテゴリを保持する
		model.addAttribute("categoryId", id);

		return "client/item/list";
	}
	
	
}
