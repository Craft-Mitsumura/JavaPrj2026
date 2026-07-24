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
 * 広告追加系
 */
@Controller
@RequestMapping("/admin/ad/regist")
@SessionAttributes("registForm") // セッション持越し用
public class AdminAdRegistController {

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
	 * コンストラクタ ディレクトリがない場合作成
	 */
	public AdminAdRegistController() {
		File tmpDir = new File(TMP_DIR);
		File uploadDir = new File(UPLOAD_DIR);

		if (!tmpDir.exists())
			tmpDir.mkdirs();
		if (!uploadDir.exists())
			uploadDir.mkdirs();
	}

	/**
	 * 広告フォーム
	 * @return 新しい広告フォーム
	 */
	@ModelAttribute("registForm")
	public PromotionsForm setUpForm() {
		return new PromotionsForm();
	}

	/**
	 * 新規登録の入り口（POST）
	 * メニュー等から最初にここへPOSTでアクセスし、古いセッションをクリアして新しいフォームをセットする
	 * @return admin/ad/regist_input
	 */
	@RequestMapping(path = "/input", method = RequestMethod.POST)
	public String registInputInit(SessionStatus status) {
		// 古いセッション状態をクリア
		status.setComplete();

		return "redirect:/admin/ad/regist/input";
	}

	/**
	 * 入力画面表示（GET）
	 * @return "admin/ad/regist_input" 
	 */
	@RequestMapping(path = "/input", method = RequestMethod.GET)
	public String registInput(@ModelAttribute("registForm") PromotionsForm form, Model model) {
		// ドロップダウン用のカテゴリ一覧をモデルに格納
		model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));

		return "admin/ad/regist_input";
	}

	/**
	 * 確認画面
	 * @param form 広告フォーム
	 * @param result 結果を保持リザルト
	 * @param model モデル
	 * @return 正常時は確認画面、エラー時は入力画面
	 * @throws IOException 入出力例外が発生した
	 */
	@RequestMapping(path = "/check", method = RequestMethod.POST)
	public String check(
			@Valid @ModelAttribute("registForm") PromotionsForm form,
			BindingResult result,
			Model model) throws IOException {

		// バリデーションチェック
		if (result.hasErrors()) {
			model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));
			model.addAttribute("registForm", form);
			return "admin/ad/regist_input";
		}

		long maxSize = 1024 * 1024; // 1MB

		// 画像サイズチェック
		if (form.getImageName() != null && form.getImageName().getSize() > maxSize) {
			model.addAttribute("errorMessage", "画像は1MB以内のものを選択してください。");
			return "admin/ad/regist_input";
		}

		try {
			// カルーセル画像の一時保存
			if (form.getImageName() != null && !form.getImageName().isEmpty()) {
				String fileName = form.getImageName().getOriginalFilename();
				if (form.getTempImageName() == null || !form.getTempImageName().equals(fileName)) {
					form.getImageName().transferTo(new File(TMP_DIR + fileName));
					form.setTempImageName(fileName);
				}
			}
			Category selectedCategory = categoryRepository.findById(form.getCategoryId()).orElse(null);
			model.addAttribute("selectedCategory", selectedCategory);
			model.addAttribute("registForm", form);

			return "admin/ad/regist_check";

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "ファイルのアップロードセッションがタイムアウトしました。お手数ですが、再度ファイルを選択してください。");
			model.addAttribute("categoryList", categoryRepository.findByDeleteFlagOrderByIdAsc(0));
			model.addAttribute("registForm", form);
			return "admin/ad/regist_input";
		}
	}

	/**
	 * 登録完了処理
	 * @param form 広告情報が格納されたフォーム 
	 * @param result 結果
	 * @param status セッションステータス
	 * @return 完了画面表示処理へのリダイレクトURL
	 * @throws Exception 登録処理で予期せぬ例外が発生した場合
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public String complete(@Valid @ModelAttribute("registForm") PromotionsForm form,
			BindingResult result,
			SessionStatus status) throws Exception {

		// DB登録用エンティティの直接生成・値の詰め替え
		Promotions entity = new Promotions();
		entity.setPageName(form.getPageName());
		entity.setImageName(form.getTempImageName()); // 一時保存されたファイル名をセット

		// フォームのcategoryIdからCategoryエンティティを取得してセット
		Category category = categoryRepository.findById(form.getCategoryId()).orElse(null);
		entity.setCategory(category);

		entity.setIsActive(form.getIsActive());

		// DB登録処理
		promotionsRepository.save(entity);

		// 一時保存された画像を本番アップロードディレクトリへ移動
		moveFile(form.getTempImageName());

		// セッションクリア
		status.setComplete();

		return "redirect:/admin/ad/regist/complete";
	}

	/**
	 * 完了画面表示
	 * @return "admin/ad/regist_complete"
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.GET)
	public String completeView() {
		return "admin/ad/regist_complete";
	}

	/**
	 * ファイル移動用の共通ヘルパーメソッド
	 * @param fileName ファイル名
	 * @throws IOException ファイル移動処理の時に例外が発生した場合
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