package jp.co.sss.shop.controller.admin.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
	 * 削除確認画面表示
	 * @param id 削除対象のID
	 * @param model モデル
	 * @return "admin/ad/delete_check"
	 */
	@RequestMapping(path = "/confirm", method = RequestMethod.POST)
	public String confirm(@RequestParam("id") Integer id, Model model) {
		// 該当データを取得
		Promotions promo = promotionsRepository.findById(id).orElse(null);
		if (promo == null) {
			return "redirect:/admin/ad/list";
		}

		// フォームに値を詰め替える（確認画面で th:object="${updateForm}" を使うため）
		PromotionsForm form = new PromotionsForm();
		form.setId(promo.getId());
		form.setPageName(promo.getPageName());
		if (promo.getCategory() != null) {
			form.setCategoryId(promo.getCategory().getId());
			Category selectedCategory = categoryRepository.findById(promo.getCategory().getId()).orElse(null);
			model.addAttribute("selectedCategory", selectedCategory);
		}
		form.setIsActive(promo.getIsActive());
		form.setTempImageName(promo.getImageName()); // 登録されている画像名を設定

		model.addAttribute("updateForm", form);

		return "admin/ad/delete_check";
	}

	/**
	 * 削除実行処理
	 * @param id 削除対象のID
	 * @return 削除完了画面へリダイレクト
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public String complete(@RequestParam("id") Integer id) {
		// 該当するデータが存在する場合に物理削除を実行
		if (promotionsRepository.existsById(id)) {
			promotionsRepository.deleteById(id);
		}

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