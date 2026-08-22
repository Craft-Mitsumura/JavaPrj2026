package jp.co.sss.shop.controller.admin.ad;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.form.PromotionsForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.PromotionsRepository;

/**
 * @author 金城（チームF）
 * 広告機能-システム管理者向け
 * 広告削除系
 */
@Controller
@RequestMapping("/admin/ad/delete")
public class AdminAdDeleteController {

	/**
	 * 広告情報を管理するリポジトリ
	 */
	@Autowired
	PromotionsRepository promotionsRepository;

	/**
	 * カテゴリ情報を管理するリポジトリ
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 削除確認画面 初期表示処理(POST)
	 * 詳細画面や一覧からIDを受け取り、フォームを生成してセッションに詰めてからGETへリダイレクト
	 * @param id 削除対象のID
	 * @return "/delete_check"
	 */
	@RequestMapping(path = "/check/{id}", method = RequestMethod.POST)
	public String deleteCheckInit(@PathVariable("id") Integer id) {
		// 該当データを取得
		Promotions promo = promotionsRepository.findById(id).orElse(null);
		if (promo == null) {
			return "redirect:/admin/ad/list";
		}

		// フォームに値を詰め替える（確認画面で th:object="${updateForm}" を使うため）
		PromotionsForm deleteform = new PromotionsForm();
		deleteform.setId(promo.getId());
		deleteform.setPageName(promo.getPageName());
		if (promo.getCategory() != null) {
			deleteform.setCategoryId(promo.getCategory().getId());
		}
		deleteform.setIsActive(promo.getIsActive());
		deleteform.setTempImageName(promo.getImageName()); // 登録されている画像名を設定

		session.setAttribute("deleteform", deleteform);

		return "redirect:/admin/ad/delete/check";
	}

	/**
	 * 削除確認画面 表示処理(GET)
	 * @param id 削除対象のID
	 * @param model モデル
	 * @return "admin/ad/delete_check"
	 */
	@RequestMapping(path = "/check", method = RequestMethod.GET)
	public String deleteCheck(Model model) {
		// 該当データを取得
		PromotionsForm deleteform = (PromotionsForm) session.getAttribute("deleteform");
		if (deleteform == null) {
			return "redirect:/admin/ad/list";
		}

		if (deleteform.getCategoryId() != null) {
			Category selectedCategory = categoryRepository.findById(deleteform.getCategoryId()).orElse(null);
			model.addAttribute("selectedCategory", selectedCategory);
		}

		model.addAttribute("deleteform", deleteform);

		return "admin/ad/delete_check";
	}

	/**
	 * 削除実行処理
	 * @param id 削除対象のID
	 * @return 削除完了画面へリダイレクト
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public String complete(SessionStatus status) {
		PromotionsForm deleteform = (PromotionsForm) session.getAttribute("deleteform");
		if (deleteform == null) {
			return "redirect:/admin/ad/list";
		}
		
		// 該当するデータが存在する場合に物理削除を実行
		if (promotionsRepository.existsById(deleteform.getId())) {
			promotionsRepository.deleteById(deleteform.getId());
		}
		
		status.setComplete();
		session.removeAttribute("deleteform");

		// 完了画面へリダイレクト
		return "redirect:/admin/ad/delete/complete";
	}

	/**
	 * 削除完了画面表示
	 * @return "admin/ad/delete_complete"
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.GET)
	public String completeView() {
		return "admin/ad/delete_complete";
	}
}