package jp.co.sss.shop.controller.admin.ad;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.form.PromotionsForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.PromotionsRepository;

/**
 * @author 金城（チームF）
 * カルーセルカテゴリ広告機能-システム管理者向け
 * 広告変更系
 */
@Controller
@RequestMapping("/admin/ad/update")
@SessionAttributes("updateForm")
public class AdminAdUpdateController {

	/**
	 * 広告情報を管理するリポジトリ
	 */
	@Autowired
	PromotionsRepository repository;

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
	 * 実行環境のルートパスを取得
	 */
	private final String BASE_PATH = System.getProperty("user.dir") + File.separator;

	/**
	 * 一時保存フォルダ
	 */
	private final String TMP_DIR = BASE_PATH + "images" + File.separator + "image_tmp" + File.separator;

	/**
	 * アップロード保存フォルダ
	 */
	private final String UPLOAD_DIR = BASE_PATH + "images" + File.separator + "uploads" + File.separator;

	/**
	 * 変更入力画面 初期表示処理(POST)
	 * 詳細画面や一覧からIDを受け取り、フォームを生成してセッションに詰めてからGETへリダイレクト
	 * @param id ID
	 * @return "redirect:/admin/ad/update/input"
	 */
	@RequestMapping(path = "/input/{id}", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {

		// セッションスコープより入力情報を取り出す
		PromotionsForm updateForm = (PromotionsForm) session.getAttribute("updateForm");

		// 新規に開いた場合（あるいは別IDの場合）、DBから取得してセッションを初期化
		Promotions promo = repository.findById(id).orElse(null);
		if (promo == null) {
			return "redirect:/syserror";
		}

		// フォームにEntityの値を直接詰め替える
		updateForm = new PromotionsForm();
		updateForm.setId(promo.getId());
		updateForm.setPageName(promo.getPageName());

		// 外部キー（Category）のIDをフォームのcategoryIdにセット
		if (promo.getCategory() != null) {
			updateForm.setCategoryId(promo.getCategory().getId());
		}

		updateForm.setIsActive(promo.getIsActive());
		updateForm.setTempImageName(promo.getImageName()); // 既存の画像名を一時画像名として保持

		// セッションに保持
		session.setAttribute("updateForm", updateForm);

		// GETメソッドへリダイレクト
		return "redirect:/admin/ad/update/input";
	}

	/**
	 * 変更入力画面 表示処理(GET)
	 * @param model モデル
	 * @return "admin/ad/update_input"
	 */
	@RequestMapping(path = "/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		// セッションから入力フォーム表示情報取得
		PromotionsForm updateForm = (PromotionsForm) session.getAttribute("updateForm");
		if (updateForm == null) {
			return "redirect:/syserror";
		}

		// バリデーションエラー情報があれば復元
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.updateForm", result);
			session.removeAttribute("result");
		}

		model.addAttribute("updateForm", updateForm);
		// ドロップダウン用のカテゴリ一覧をモデルに格納
		model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));

		return "admin/ad/update_input";
	}

	/**
	 * 確認画面：バリデーションとファイルの一時保存
	 * @param form 広告フォーム
	 * @param result バインドリザルト
	 * @param model モデル
	 * @return エラー時は入力画面へリダイレクト、成功時は確認画面へリダイレクト
	 */
	@RequestMapping(path = "/check", method = RequestMethod.POST)
	public String check(@Valid @ModelAttribute("updateForm") PromotionsForm form,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			session.setAttribute("result", result);
			return "redirect:/admin/ad/update/input";
		}

		long maxSize = 1024 * 1024; // 1MB

		// 画像サイズチェック
		if (form.getImageName() != null && form.getImageName().getSize() > maxSize) {
			// エラー時はセッション等にメッセージを詰めるか、簡易的に戻す
			return "redirect:/admin/ad/update/input";
		}

		try {
			// カルーセル画像の保存
			if (form.getImageName() != null && !form.getImageName().isEmpty()) {
				String fileName = form.getImageName().getOriginalFilename();
				if (form.getTempImageName() == null || !form.getTempImageName().equals(fileName)) {
					form.getImageName().transferTo(new File(TMP_DIR + fileName));
					form.setTempImageName(fileName);
				}
			}

			return "redirect:/admin/ad/update/check";

		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/admin/ad/update/input";
		}
	}

	/**
	 * 変更確認画面 表示処理 (GET)
	 * @param model モデル
	 * @return "admin/ad/update_check"
	 */
	@RequestMapping(path = "/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {
		PromotionsForm updateForm = (PromotionsForm) session.getAttribute("updateForm");
		if (updateForm == null) {
			return "redirect:/syserror";
		}

		if (updateForm.getIsActive() == null) {
			updateForm.setIsActive(0);
		}

		Category selectedCategory = categoryRepository.findById(updateForm.getCategoryId()).orElse(null);
		model.addAttribute("selectedCategory", selectedCategory);
		model.addAttribute("updateForm", updateForm);

		// 画像の表示パスを判定して渡す
		String imagePath = "/images/common/no_image.jpg";
		if (updateForm.getTempImageName() != null && !updateForm.getTempImageName().isEmpty()) {
			File tmpFile = new File(TMP_DIR + updateForm.getTempImageName());
			if (tmpFile.exists()) {
				imagePath = "/images/image_tmp/" + updateForm.getTempImageName();
			} else {
				imagePath = "/images/uploads/" + updateForm.getTempImageName();
			}
		}
		model.addAttribute("imagePath", imagePath);

		return "admin/ad/update_check";
	}

	/**
	 * 終了処理（更新完了）(POST)
	 * @param status セッションステータス
	 * @return "redirect:/admin/ad/update/complete"
	 * @throws Exception 例外
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public String complete(SessionStatus status) throws Exception {

		PromotionsForm form = (PromotionsForm) session.getAttribute("updateForm");
		if (form == null) {
			return "redirect:/syserror";
		}

		// 既存データを取得して内容を更新
		Promotions promo = repository.findById(form.getId()).get();
		promo.setPageName(form.getPageName());
		promo.setImageName(form.getTempImageName()); // 一時保存されたファイル名をセット

		// フォームのcategoryIdからCategoryエンティティを取得してセット
		Category category = categoryRepository.findById(form.getCategoryId()).orElse(null);
		promo.setCategory(category);

		promo.setIsActive(form.getIsActive());

		repository.save(promo);

		// ファイル移動
		moveFile(form.getTempImageName());

		// セッションクリア
		status.setComplete();
		session.removeAttribute("updateForm");

		return "redirect:/admin/ad/update/complete";
	}

	/**
	 * 完了画面表示 (GET)
	 * @return "admin/ad/update_complete"
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.GET)
	public String completeView() {
		return "admin/ad/update_complete";
	}

	/**
	 * ファイル移動用の共通ヘルパーメソッド
	 * @param fileName ファイル名
	 * @throws IOException 移動中に例外が発生した場合
	 */
	private void moveFile(String fileName) throws IOException {
		if (fileName != null && !fileName.isEmpty()) {
			Path source = Paths.get(TMP_DIR + fileName);
			Path target = Paths.get(UPLOAD_DIR + fileName);
			if (Files.exists(source)) {
				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}
}