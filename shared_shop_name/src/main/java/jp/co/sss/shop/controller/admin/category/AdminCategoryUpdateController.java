package jp.co.sss.shop.controller.admin.category;

import java.sql.Date;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.CategoryBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.form.CategoryForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * カテゴリ管理 変更機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminCategoryUpdateController {

	/**
	 * カテゴリ情報　リポジトリ
	 */
	@Autowired
	CategoryRepository categoryRepository;

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
	 * 入力画面　初期表示処理(POST)
	 * 
	 * @param id 変更対象ID
	 * @return "redirect:/admin/category/update/input" 入力録画面　表示処理
	 */
	@RequestMapping(path = "/admin/category/update/input/{id}", method = RequestMethod.POST)
	public String updateInput(@PathVariable Integer id) {

		//セッションスコープより入力情報を取り出す
		CategoryForm categoryForm = (CategoryForm) session.getAttribute("categoryForm");

		if (categoryForm == null) {
			// セッション情報がない場合、詳細画面からの遷移と判断し初期値を準備

			// 変更対象の情報を取得
			Category category = categoryRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

			if (category == null) {
				// 対象が無い場合、エラー
				return "redirect:/syserror";
			}

			// 初期表示用フォーム情報の生成しIDで取得した値を入力フォーム情報としてセット
			categoryForm = new CategoryForm();
			BeanUtils.copyProperties(category, categoryForm);

			//変更入力フォームをセッションに保持
			session.setAttribute("categoryForm", categoryForm);
		}

		//変更入力画面　表示処理
		return "redirect:/admin/category/update/input";

	}

	/**
	 * 入力画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/category/update_input" 更新入力画面表示
	 */
	@RequestMapping(path = "/admin/category/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから入力フォーム取得
		CategoryForm categoryForm = (CategoryForm) session.getAttribute("categoryForm");
		if (categoryForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.categoryForm", result);
			// セッションのエラー情報を削除
			session.removeAttribute("result");
		}

		// 入力フォーム情報を画面表示設定
		model.addAttribute("categoryForm", categoryForm);

		//変更入力画面　表示
		return "admin/category/update_input";

	}

	/**
	 * 変更確認 処理
	 *
	 * @param form 入力フォーム
	 * @param result 入力チェック結果
	 * @return 
	 *   入力値エラーあり："redirect:/admin/category/update/input" 変更入力画面へ 
	 *   入力値エラーなし："redirect:/admin/category/update/check" 変更確認画面へ
	 */
	@RequestMapping(path = "/admin/category/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute CategoryForm form, BindingResult result) {

		// 入力されたカテゴリ情報をセッションに保持
		session.setAttribute("categoryForm", form);

		if (result.hasErrors()) {
			// 入力値にエラーがあった場合、エラー情報をセッションに保持
			session.setAttribute("result", result);

			//変更入力画面　表示処理
			return "redirect:/admin/category/update/input";
		}

		//変更確認画面　表示処理
		return "redirect:/admin/category/update/check";
	}

	/**
	 * 確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/category/update_check" 確認画面 表示
	 */
	@RequestMapping(path = "/admin/category/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {
		//セッションから入力フォーム情報取得
		CategoryForm categoryForm = (CategoryForm) session.getAttribute("categoryForm");
		if (categoryForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		//入力フォーム情報をスコープへ設定
		model.addAttribute("categoryForm", categoryForm);

		// 変更確認画面　表示
		return "admin/category/update_check";

	}

	/**
	 * 変更登録、完了画面表示処理
	 *
	 * @return "redirect:/admin/category/update/complete" 変更完了画面　表示へ
	 */
	@RequestMapping(path = "/admin/category/update/complete", method = RequestMethod.POST)
	public String updateComplete() {

		//セッション保持情報から入力値再取得
		CategoryForm categoryForm = (CategoryForm) session.getAttribute("categoryForm");
		if (categoryForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 変更対象の情報取得
		Category category = categoryRepository.findByIdAndDeleteFlag(categoryForm.getId(), Constant.NOT_DELETED);
		if (category == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// 入力値以外の情報を一時退避
		Integer deleteFlag = category.getDeleteFlag();
		Date insertDate = category.getInsertDate();

		// 入力フォーム情報を変更用エンティティにコピー
		BeanUtils.copyProperties(categoryForm, category);

		// 入力値以外の情報を設定
		category.setDeleteFlag(deleteFlag);
		category.setInsertDate(insertDate);

		// カテゴリ情報を保存
		categoryRepository.save(category);

		//セッション情報の削除
		session.removeAttribute("categoryForm");

		// カテゴリ情報を全件検索
		List<Category> categoryList = categoryRepository
				.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);

		// エンティティ内の検索結果をJavaBeansにコピー
		List<CategoryBean> categoryBeanList = beanTools.copyEntityListToCategoryBeanList(categoryList);

		// セッションスコープ中に保存されたカテゴリ一覧の情報を更新
		session.setAttribute("categories", categoryBeanList);

		// 変更完了画面　表示処理
		//二重送信対策のためリダイレクトを行う
		return "redirect:/admin/category/update/complete";

	}

	/**
	 * 変更完了画面　表示
	 * 
	 * @return "admin/category/update_complete"
	 */
	@RequestMapping(path = "/admin/category/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {

		return "admin/category/update_complete";
	}

}
