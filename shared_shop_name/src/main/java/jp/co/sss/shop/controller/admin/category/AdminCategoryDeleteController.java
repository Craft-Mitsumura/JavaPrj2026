package jp.co.sss.shop.controller.admin.category;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * カテゴリ管理 削除機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminCategoryDeleteController {

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
	 * 削除確認画面 受付処理
	 *
	 * @param id 削除対象ID
	 * @return "redirect:/admin/category/delete/check" 削除確認画面　表示
	 */
	@RequestMapping(path = "/admin/category/delete/check/{id}", method = RequestMethod.POST)
	public String deleteCheck(@PathVariable Integer id) {

		// 削除対象の情報取得
		Category category = categoryRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (category == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// 取得情報から表示フォーム情報を生成
		CategoryForm categoryForm = new CategoryForm();
		//取得情報をフォームにコピー
		BeanUtils.copyProperties(category, categoryForm);

		// 表示フォーム情報をセッションに保持
		session.setAttribute("categoryForm", categoryForm);

		// 削除確認画面　表示処理
		return "redirect:/admin/category/delete/check";
	}

	/**
	 * 確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/category/delete_check" 削除確認画面　表示
	 */
	@RequestMapping(path = "/admin/category/delete/check", method = RequestMethod.GET)
	public String deleteCheckView(Model model) {

		//セッションから入力フォーム取得
		CategoryForm categoryForm = (CategoryForm) session.getAttribute("categoryForm");
		if (categoryForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 入力フォーム情報を画面表示設定
		model.addAttribute("categoryForm", categoryForm);

		// 削除確認画面　表示
		return "admin/category/delete_check";
	}

	/**
	 * 削除処理、完了画面　表示処理
	 *
	 * @return "redirect:/admin/category/delete/complete" 削除完了画面　表示処理
	 */
	@RequestMapping(path = "/admin/category/delete/complete", method = RequestMethod.POST)
	public String deletetComplete() {

		// セッションから削除対象フォーム情報を取得
		CategoryForm categoryForm = (CategoryForm) session.getAttribute("categoryForm");
		if (categoryForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 削除対象の情報取得
		Category category = categoryRepository.findByIdAndDeleteFlag(categoryForm.getId(), Constant.NOT_DELETED);

		if (category == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// カテゴリ削除フラグを立てる情報を取得
		category.setDeleteFlag(Constant.DELETED);

		// カテゴリ情報を保存
		categoryRepository.save(category);

		// カテゴリ情報を全件検索
		List<Category> categoryList = categoryRepository
				.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);

		// エンティティ内の検索結果をJavaBeansにコピー
		List<CategoryBean> categoryBeanList = beanTools.copyEntityListToCategoryBeanList(categoryList);

		// セッションスコープ中に保存されたカテゴリ一覧の情報を更新
		session.setAttribute("categories", categoryBeanList);

		// セッションの削除対象情報を削除
		session.removeAttribute("categoryForm");

		// 削除完了画面　表示処理
		//二重送信対策のためリダイレクトを行う
		return "redirect:/admin/category/delete/complete";
	}

	/**
	 * 削除完了画面 表示
	 *
	 * @return "admin/category/delete_complete" 削除完了画面　表示
	 */
	@RequestMapping(path = "/admin/category/delete/complete", method = RequestMethod.GET)
	public String deletetCompleteRedirect() {

		return "admin/category/delete_complete";
	}

}
