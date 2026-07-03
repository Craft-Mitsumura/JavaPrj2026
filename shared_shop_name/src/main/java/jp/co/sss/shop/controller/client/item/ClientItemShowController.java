package jp.co.sss.shop.controller.client.item;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.FavoriteService;

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
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * お気に入り情報を操作するためのサービス部材
	 */
	@Autowired
	FavoriteService favoriteService;

	/**
	 * ログインユーザーのセッション情報を管理するオブジェクト
	 */
	@Autowired
	HttpSession session;

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

	@RequestMapping(path = "/client/item/list/1", method = { RequestMethod.GET, RequestMethod.POST })
	public String showAll(Model model) {
		model.addAttribute("items", itemRepository.findAll());
		return "client/item/list";
	}

	/**
	 * 指定された商品IDの詳細画面を表示します。
	 * ログイン状態であれば、該当商品が既にお気に入りに登録されているかの判定も合わせて行います。
	 * @author 吉浜, 田中（チームF）
	 * @param id    表示対象の商品ID
	 * @param model 画面へデータを渡すためのModelオブジェクト
	 * @return 商品詳細画面のHTMLパス ("client/item/detail")
	 */
	@RequestMapping(path = "/client/item/detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable Integer id, Model model) {

		// 削除フラグが立っていない該当商品を取得
		Item item = itemRepository.findByIdAndDeleteFlag(id, 0);
		model.addAttribute("item", item);

		// セッションからログインユーザー情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");
		boolean isFavorite = false;

		// ログイン状態の場合のみ、お気に入り登録済みかチェック
		if (loginUser != null) {
			List<Favorite> favorites = favoriteService.getFavoriteList(loginUser.getId());
			if (favorites != null) {
				isFavorite = favorites.stream()
						.anyMatch(f -> f.getItem().getId().equals(id));
			}
		}

		model.addAttribute("isFavorite", isFavorite);

		return "client/item/detail";
	}

}
