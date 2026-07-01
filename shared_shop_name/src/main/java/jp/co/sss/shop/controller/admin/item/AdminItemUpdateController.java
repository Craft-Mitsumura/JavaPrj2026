package jp.co.sss.shop.controller.admin.item;

import java.sql.Date;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.form.ItemForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.UploadFileService;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 変更機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminItemUpdateController {

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
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * ファイルアップロードサービス
	 */
	@Autowired
	UploadFileService upfileService;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 変更入力画面　初期表示処理(POST)
	 * 
	 * @param id 変更対象ID
	 * @return "redirect:/admin/item/update/input" 変更入力画面　表示処理
	 */
	@RequestMapping(path = "/admin/item/update/input/{id}", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {

		//セッションスコープより入力情報を取り出す
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");

		if (itemForm == null) {
			// セッション情報がない場合、詳細画面からの遷移と判断し初期値を準備

			// 変更対象の情報を取得
			Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

			if (item == null) {
				// 対象が無い場合、エラー
				return "redirect:/syserror";
			}

			// 初期表示用フォーム情報の生成しIDで取得した値を入力フォーム情報としてセット
			itemForm = beanTools.copyEntityToItemForm(item);
			//変更入力フォームをセッションに保持
			session.setAttribute("itemForm", itemForm);
		}

		// 変更入力画面　表示処理
		return "redirect:/admin/item/update/input";

	}

	/**
	 * 変更入力画面　表示処理(GET)
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/item/update_input" 変更入力画面 表示
	 */
	@RequestMapping(path = "/admin/item/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから入力フォーム表示情報取得
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");
		if (itemForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.itemForm", result);
			// セッションのエラー情報を削除
			session.removeAttribute("result");
		}

		model.addAttribute("itemForm", itemForm);

		// 変更入力画面　表示
		return "admin/item/update_input";

	}

	/**
	 * 変更入力確認処理
	 *
	 * @param form 入力フォーム
	 * @param result 入力チェック結果
	 * @return 
	 *   入力値エラーあり："redirect:/admin/item/update/input" 変更入力画面　表示処理 
	 *   入力値エラーなし："redirect:/admin/item/update/check" 変更確認画面　表示処理
	 */
	@RequestMapping(path = "/admin/item/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute ItemForm form, BindingResult result) {

		//直前のセッション情報を取得
		ItemForm lastItemForm = (ItemForm) session.getAttribute("itemForm");
		if (lastItemForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}

		// 入力された情報をセッションに保持
		session.setAttribute("itemForm", form);

		if (result.hasErrors()) {
			// 入力値にエラーがあった場合、エラー情報をセッションに保持し、変更入力画面を表示
			session.setAttribute("result", result);
			return "redirect:/admin/item/update/input";

		}
		//ファイルアップロード処理呼び出す 戻り値 成功時:ファイル名、失敗時:null
		String imageName = upfileService.saveUploadFile(form.getImageFile());
		if (imageName != null) {

			// 一時的にアップロードしたファイルの名前をFormクラスにセット
			form.setImage(imageName);
		} else {
			// イメージが入力されなかった場合に、直前に登録されていたイメージファイルを設定しておく
			form.setImage(lastItemForm.getImage());
		}

		// 選択したカテゴリの名前をFormクラスにセット
		if (form.getCategoryId() != null) {
			Category category = categoryRepository.findById(form.getCategoryId()).orElse(null);
			form.setCategoryName(category.getName());
		}

		// 変更入力確認画面　表示処理
		return "redirect:/admin/item/update/check";
	}

	/**
	 * 変更確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/item/update_check" 確認画面表示
	 */
	@RequestMapping(path = "/admin/item/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {
		//セッションから情報取得
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");
		if (itemForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}

		model.addAttribute("itemForm", itemForm);

		// 変更確認画面　表示
		return "admin/item/update_check";

	}

	/**
	 * 変更登録、完了画面表示処理
	 * 
	 * @return "redirect:/admin/item/update/complete" 変更完了画面　表示
	 */
	@RequestMapping(path = "/admin/item/update/complete", method = RequestMethod.POST)
	public String updateComplete() {

		//セッション保持情報から入力値再取得
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");
		if (itemForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}

		// 変更対象の情報を取得
		Item item = itemRepository.findByIdAndDeleteFlag(itemForm.getId(), Constant.NOT_DELETED);

		if (item == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}
		// 入力値以外の情報を一時退避
		Integer deleteFlag = item.getDeleteFlag();
		Date insertDate = item.getInsertDate();

		// 入力フォーム情報を変更用エンティティにコピー
		item = beanTools.copyItemFormToEntity(itemForm);
		// 入力値以外の情報をエンティティに設定
		item.setDeleteFlag(deleteFlag);
		item.setInsertDate(insertDate);

		// 商品情報を保存
		itemRepository.save(item);

		//セッション情報の削除
		session.removeAttribute("itemForm");

		// 変更完了画面　表示処理
		//二重送信対策のためリダイレクトを行う
		return "redirect:/admin/item/update/complete";
	}

	/**
	 * 変更完了画面　表示
	 * 
	 * @return "admin/item/update_complete"
	 */
	@RequestMapping(path = "/admin/item/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
		return "admin/item/update_complete";
	}

}
