package jp.co.sss.shop.controller.client.favorite;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.service.FavoriteService;
import jp.co.sss.shop.util.Constant;

/**
 * お気に入り管理 一覧表示機能(一般会員用)のコントローラクラス
 * @author 田中（チームF）
 */
@Controller
public class ClientFavoriteController {

	/** お気に入り管理用サービス */
	@Autowired
	private FavoriteService favoriteService;

	/** カテゴリ情報管理するリポジトリ */
	@Autowired
	private CategoryRepository categoryRepository;

	/** ログインユーザのセッション情報 */
	@Autowired
	private HttpSession session;

	/**
	 * お気に入り商品一覧画面 表示（カテゴリ絞り込み機能付き）
	 * @param   categoryId 絞り込み対象のカテゴリID（未指定時はnull）
	 * @param   page ページ番号
	 * @param　 model データを保持するモデル
	 * @return　お気に入り商品一覧表示画面
	 */
	@GetMapping("/client/favorite/list")
	public String list(@RequestParam(name = "categoryId", required = false) Integer categoryId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			Model model) {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// ログインユーザーのお気に入り一覧を全件取得
		List<Favorite> favoriteList = favoriteService.getFavoriteList(loginUser.getId());

		// カテゴリIDが選択されている（かつ「すべて(0)」ではない）場合、お気に入りデータを絞り込む
		if (categoryId != null && categoryId != 0) {
			favoriteList = favoriteList.stream()
					.filter(f -> f.getItem().getCategory().getId().equals(categoryId))
					.collect(Collectors.toList());
		}

		//ページの処理
		int totalItems = favoriteList.size(); // 絞り込み後のお気に入りの総件数
		int pageSize = 10; // 1ページあたりの表示件数
		// 全体のページ数を計算（例：11件なら 2ページ になる）
		int totalPages = (int) Math.ceil((double) totalItems / pageSize);

		// 次の10件だけを取得します
		List<Favorite> pagedFavoriteList = favoriteList.stream()
				.skip((long) page * pageSize) // 1ページ目なら0件スキップ、2ページ目なら10件スキップ
				.limit(pageSize) // そこから最大10件だけ残す
				.collect(Collectors.toList());

		// HTMLにデータを渡す
		model.addAttribute("favoriteList", pagedFavoriteList); // 10件に制限されたリスト
		model.addAttribute(
			    "categories",
			    categoryRepository.findByDeleteFlagOrderByInsertDateDescIdAsc(Constant.NOT_DELETED)
			);
		model.addAttribute("categoryId", categoryId);

		// ページめくりボタンを作るための情報を新しく送る
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);

		return "client/favorite/list";
	}

	/**
	 * お気に入り追加・解除の非同期トグル処理
	 * @param  itemId 対象商品ID
	 * @return 処理結果のステータス文字列
	 */
	@PostMapping("/client/favorite/add")
	@ResponseBody
	public String add(@RequestParam("itemId") Integer itemId) {

	    UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {
			return "ng"; // 未ログイン 
		}

		// 現在のお気に入り一覧を取得
		List<Favorite> favoriteList = favoriteService.getFavoriteList(loginUser.getId());

		// 既に「有効な状態（deleteFlagが0）」でお気に入りに存在するかチェック
		boolean isFavorite = false;
		if (favoriteList != null) {
			isFavorite = favoriteList.stream()
					.anyMatch(f -> f.getItem().getId().equals(itemId)
							&& (f.getDeleteFlag() == null || f.getDeleteFlag() == 0));
		}

		//「追加」と「解除」を切り替える 
		if (isFavorite) {

		    favoriteService.removeFavorite(loginUser.getId(), itemId);

		    List<Favorite> favorites =
		            favoriteService.getFavoriteList(loginUser.getId());

		    session.setAttribute("favoriteBeans", favorites);

		    return "detached";

		} else {

		    favoriteService.addFavorite(loginUser.getId(), itemId);

		    List<Favorite> favorites =
		            favoriteService.getFavoriteList(loginUser.getId());

		    session.setAttribute("favoriteBeans", favorites);

		    return "added";
		}
	}
}
