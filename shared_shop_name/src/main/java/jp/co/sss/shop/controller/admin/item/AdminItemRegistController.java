package jp.co.sss.shop.controller.admin.item;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
 * 商品管理 登録機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminItemRegistController {

	/**
	 * サーブレットコンテキスト
	 */
	@Autowired
	ServletContext context;

	/**
	 * カテゴリ情報　リポジトリ
	 */
	@Autowired
	public CategoryRepository categoryRepository;

	/**
	 * 商品情報　リポジトリ
	 */
	@Autowired
	public ItemRepository itemRepository;

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
	 * 入力画面　表示処理(POST)
	 * 
	 * @return "redirect:/admin/item/regist/input" 入力画面　表示処理
	 */
	@RequestMapping(path = "/admin/item/regist/input", method = RequestMethod.POST)
	public String registInput() {

		//セッションスコープより入力情報を取り出す
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");

		if (itemForm == null) {
			//空の入力フォーム情報をセッションに保持 登録ボタンからの遷移
			session.setAttribute("itemForm", new ItemForm());

		}

		//登録入力画面　表示処理
		return "redirect:/admin/item/regist/input";

	}

	/**
	 * 入力画面　表示処理(GET)
	 * 
	 * @param model Viewとの値受渡し
	 * @return "admin/item/regist_input" 入力画面　表示
	 */
	@RequestMapping(path = "/admin/item/regist/input", method = RequestMethod.GET)
	public String registInput(Model model) {
		//セッションから入力フォーム情報取得
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");
		if (itemForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		//セッションから入力チェックエラー情報取得
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.itemForm", result);
			//セッションからエラー情報を削除
			session.removeAttribute("result");
		}
		// 入力フォーム情報をスコープに設定
		model.addAttribute("itemForm", itemForm);

		// 入力画面　表示
		return "admin/item/regist_input";

	}

	/**
	 * 登録入力確認処理
	 *
	 * @param form 入力フォーム
	 * @param result 入力値チェックの結果
	 * @return 
	 * 	入力値エラーあり："redirect:/admin/item/regist/input" 入力録画面　表示処理
	 * 	入力値エラーなし："redirect:/admin/item/regist/check" 登録確認画面　表示処理
	 */
	@RequestMapping(path = "/admin/item/regist/check", method = RequestMethod.POST)
	public String registInputCheck(@Valid @ModelAttribute ItemForm form, BindingResult result) {

		// 選択したカテゴリの名前をFormクラスにセット
		if (form.getCategoryId() != null) {
			//カテゴリーIDで検索し、結果が無ければnullを返す
			Category category = categoryRepository.findById(form.getCategoryId()).orElse(null);
			form.setCategoryName(category.getName());
		}

		//直前のセッション情報を取得
		ItemForm lastItemForm = (ItemForm) session.getAttribute("itemForm");
		if (lastItemForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}

		// 入力フォームをセッションに保持
		session.setAttribute("itemForm", form);

		if (result.hasErrors()) {
			// 入力値にエラーがあった場合、エラー情報をセッションに保持
			session.setAttribute("result", result);
			// 登録入力画面　表示処理
			return "redirect:/admin/item/regist/input";
		}

		//ファイルアップロード処理呼び出す 戻り値 成功時:ファイル名、失敗時:null
		String imageName = upfileService.saveUploadFile(form.getImageFile());
		if (imageName != null) {
			//ファイルアップロードが正常にできた場合
			// 一時的にアップロードしたファイルの名前をFormクラスにセット
			form.setImage(imageName);
		} else {
			// イメージが入力されなかった場合に、直前に登録されていたイメージファイルを設定しておく
			form.setImage(lastItemForm.getImage());
		}
		// 登録確認画面　表示処理 
		return "redirect:/admin/item/regist/check";
	}

	/**
	 * 登録確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/item/regist_check" 登録確認画面表示
	 */
	@RequestMapping(path = "/admin/item/regist/check", method = RequestMethod.GET)
	public String registCheck(Model model) {
		//セッションから入力フォーム情報取得
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");
		if (itemForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		//入力フォーム情報をスコープへ設定
		model.addAttribute("itemForm", itemForm);

		//登録確認画面　表示処理
		return "admin/item/regist_check";

	}

	/**
	 * 情報登録処理
	 *
	 * @return "redirect:/admin/item/regist/complete" 登録完了画面　表示処理
	 */
	@RequestMapping(path = "/admin/item/regist/complete", method = RequestMethod.POST)
	public String registComplete() {

		//セッション保持情報から入力値再取得
		ItemForm itemForm = (ItemForm) session.getAttribute("itemForm");
		if (itemForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// Formクラス内の各フィールドの値をエンティティにコピー
		Item item = beanTools.copyItemFormToEntity(itemForm);

		// 商品情報の削除フラグを初期化
		item.setDeleteFlag(Constant.NOT_DELETED);

		// 商品情報をDBに保存
		itemRepository.save(item);

		//セッション情報の削除
		session.removeAttribute("itemForm");

		//登録完了画面　表示処理
		//二重送信対策のためリダイレクトを行う
		return "redirect:/admin/item/regist/complete";
	}

	/**
	 * 登録完了画面　表示処理
	 *
	 * @return "admin/item/regist_complete" 登録完了画面　表示
	 */
	@RequestMapping(path = "/admin/item/regist/complete", method = RequestMethod.GET)
	public String registCompleteFinish() {

		//登録完了画面　表示
		return "admin/item/regist_complete";
	}

}
