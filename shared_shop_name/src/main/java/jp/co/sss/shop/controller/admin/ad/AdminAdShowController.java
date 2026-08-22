package jp.co.sss.shop.controller.admin.ad;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.repository.PromotionsRepository;

/**
 * @author	金城（チームF）
 * 広告機能-システム管理者向け
 * 広告表示系
 * 
 */

@Controller
public class AdminAdShowController {

	/**
	 * 広告情報
	 */
	@Autowired
	PromotionsRepository repository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 広告一覧表示
	 * URL: /admin/ad/list
	 * @param model モデル
	 * @return 広告一覧
	 */
	@GetMapping("/admin/ad/list")
	public String list(Model model) {
		// ▼ 一覧画面に戻ってきたタイミングで古いセッションをクリアする
		session.removeAttribute("updateForm");
		session.removeAttribute("result");

		// 削除フラグを引数に検索
		model.addAttribute("promotions", repository.findAllByOrderByIsActiveDescIdDesc());

		return "admin/ad/list";
	}

	/**
	 * 広告詳細表示
	 * URL: /admin/ad/details/{id}
	 * @param id 広告ID
	 * @param model モデル
	 * @return 広告詳細画面
	 */
	@RequestMapping(path = "/admin/ad/details/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String adDetails(@PathVariable Integer id, Model model) {
		// ▼ 一覧画面に戻ってきたタイミングで古いセッションをクリアする
		session.removeAttribute("updateForm");
		session.removeAttribute("result");

		// 表示対象の広告情報を取得（存在しない場合はエラーページ等へリダイレクト）
		Promotions promotion = repository.findById(id).orElse(null);

		if (promotion == null) {
			// 対象が無い場合の安全なハンドリング
			return "redirect:/syserror";
		}

		// 広告情報をViewへ渡す
		model.addAttribute("promotion", promotion);

		// 必要に応じて関連するフォームや不要なセッションのクリアを行う場合
		// session.removeAttribute("registForm");
		// session.removeAttribute("updateForm");

		return "admin/ad/adDetails";
	}

}
