package jp.co.sss.shop.controller.admin.ad;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@SessionAttributes("updateForm") // セッション持越しを有効化（登録側と統一）
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
	 * 入力画面：IDを受け取ってセッションに詰める
	 * @param id ID
	 * @param model モデル
	 * @param status ステータス
	 * @return "admin/ad/update_input"
	 * @throws Exception ID受け取りに例外が発生した場合
	 */
	@RequestMapping(path = "/input", method = RequestMethod.POST)
	public String input(Integer id, Model model, SessionStatus status) throws Exception {
		Promotions promo = repository.findById(id).get();

		// フォームにEntityの値を直接詰め替える
		PromotionsForm form = new PromotionsForm();
		form.setId(promo.getId());
		form.setPageName(promo.getPageName());

		// 外部キー（Category）のIDをフォームのcategoryIdにセット
		if (promo.getCategory() != null) {
			form.setCategoryId(promo.getCategory().getId());
		}

		form.setIsActive(promo.getIsActive());
		form.setTempImageName(promo.getImageName()); // 既存の画像名を一時画像名として保持

		model.addAttribute("updateForm", form);

		// ドロップダウン用のカテゴリ一覧をモデルに格納
		model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));

		return "admin/ad/update_input";
	}

	/**
	 * 確認画面：バリデーションとファイルの一時保存
	 * @param form 広告フォーム
	 * @param result バインドリザルト
	 * @param model モデル
	 * @return "admin/ad/update_input"
	 * @throws IOException 確認中に例外が発生した場合
	 */
	@RequestMapping(path = "/check", method = RequestMethod.POST)
	public String check(@Valid @ModelAttribute("updateForm") PromotionsForm form,
			BindingResult result, Model model) throws IOException {

		if (result.hasErrors()) {
			model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));
			return "admin/ad/update_input";
		}

		long maxSize = 1024 * 1024; // 1MB

		// 画像サイズチェック
		if (form.getImageName() != null && form.getImageName().getSize() > maxSize) {
			model.addAttribute("errorMessage", "画像は1MB以内のものを選択してください。");
			model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));
			return "admin/ad/update_input";
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
			Category selectedCategory = categoryRepository.findById(form.getCategoryId()).orElse(null);
			model.addAttribute("selectedCategory", selectedCategory);

			return "admin/ad/update_check";

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "ファイルのアップロードセッションがタイムアウトしました。お手数ですが、再度ファイルを選択してください。");
			model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));
			return "admin/ad/update_input";
		}
	}

	/**
	 * 終了処理（更新完了）
	 * @param form 広告フォーム
	 * @param result バインドリザルト
	 * @param status セッションステータス
	 * @return 完了画面 
	 * @throws Exception 登録処理で予期せぬ例外が発生した場合 
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public String complete(@Valid @ModelAttribute("updateForm") PromotionsForm form,
			BindingResult result,
			SessionStatus status) throws Exception {

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

		return "redirect:/admin/ad/update/complete";
	}

	/**
	 * 完了画面表示
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