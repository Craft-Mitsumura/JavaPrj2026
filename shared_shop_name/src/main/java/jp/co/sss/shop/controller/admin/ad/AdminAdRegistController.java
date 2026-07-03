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

import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.form.PromotionsForm;
import jp.co.sss.shop.repository.PromotionsRepository;
import jp.co.sss.shop.service.PromotionConverter;

/**
 * @author	金城（チームF）
 * 広告機能-システム管理者向け
 * 広告追加系
 * 
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
	 * 広告コンバータ
	 */
	@Autowired
	private PromotionConverter promotionConverter;

	/**
	 * 実行環境のルートパスを取得 
	 */
	private final String BASE_PATH = System.getProperty("user.dir") + File.separator;
	
	/**
	 * プロジェクトの images フォルダを指すように修正
	 */
	private final String TMP_DIR = BASE_PATH + "images" + File.separator + "image_tmp" + File.separator;
	
	/**
	 * プロジェクトの images フォルダを指すように修正
	 */
	private final String UPLOAD_DIR = BASE_PATH + "images" + File.separator + "uploads" + File.separator;

	/**
	 * コンストラクタ ディレクトリがない場合作動
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
	 * 入力画面
	 * @param form 広告フォーム
	 * @param status セッション
	 * @return "admin/ad/regist_input"
	 */
	@RequestMapping("/input")
	public String input(@ModelAttribute("promotionsForm") PromotionsForm form, SessionStatus status) {
		status.setComplete(); // 入力画面に来たら必ずクリア
		// 初回アクセス（レイアウトタイプがnull）なら初期値をセット
		form.setIsActive(1);
		form.setLayoutType(1);
		form.setImageName(null);
		form.setHeadingImage(null);

		form.setTempImageName(null);
		form.setTempHeadingImage(null);
		form.setTempImageSrcs(null);

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
			@Valid @ModelAttribute("promotionsForm") PromotionsForm form,
			BindingResult result,
			Model model) throws IOException {

		// バリデーションチェック、画像サイズチェック
		if (result.hasErrors()) {
			// エラーがある場合は変換処理などをスキップして入力画面へ戻る
			return "admin/ad/regist_input";
		}
		
	    long maxSize = 1024 * 1024;
	    
	    // サムネイルとタイトル画像のチェック
	    if (form.getImageName() != null && form.getImageName().getSize() > maxSize) {
	        model.addAttribute("errorMessage", "サムネイル画像は1MB以内のものを選択してください。");
	        return "admin/ad/regist_input";
	    }
	    if (form.getHeadingImage() != null && form.getHeadingImage().getSize() > maxSize) {
	        model.addAttribute("errorMessage", "タイトル画像は1MB以内のものを選択してください。");
	        return "admin/ad/regist_input";
	    }
	    // 動的画像リストのチェック
	    if (form.getImageSrcs() != null) {
	        for (org.springframework.web.multipart.MultipartFile file : form.getImageSrcs()) {
	            if (file != null && file.getSize() > maxSize) {
	                model.addAttribute("errorMessage", "動的画像の中に1MBを超えるファイルが含まれています。");
	                return "admin/ad/regist_input";
	            }
	        }
	    }
	    // ----------------------------------------------------

		try {
			// カルーセル画像の保存
			if (form.getImageName() != null && !form.getImageName().isEmpty()) {
				String fileName = form.getImageName().getOriginalFilename();
				// 重複チェックを追加（既にある場合は再保存しない）
				if (form.getTempImageName() == null || !form.getTempImageName().equals(fileName)) {
					form.getImageName().transferTo(new File(TMP_DIR + fileName));
					form.setTempImageName(fileName);
				}
			}

			// タイトル画像の保存
			if (form.getHeadingImage() != null && !form.getHeadingImage().isEmpty()) {
				String fileName = form.getHeadingImage().getOriginalFilename();
				if (form.getTempHeadingImage() == null || !form.getTempHeadingImage().equals(fileName)) {
					form.getHeadingImage().transferTo(new File(TMP_DIR + fileName));
					form.setTempHeadingImage(fileName);
				}
			}

			// 動的画像リストの保存処理
			if (form.getImageSrcs() != null && !form.getImageSrcs().isEmpty()) {
				java.util.List<String> tempFileNames = new java.util.ArrayList<>();
				for (org.springframework.web.multipart.MultipartFile file : form.getImageSrcs()) {
					if (file != null && !file.isEmpty()) {
						String fileName = file.getOriginalFilename();
						file.transferTo(new File(TMP_DIR + fileName));
						tempFileNames.add(fileName);
					} else {
						tempFileNames.add("");
					}
				}
				form.setTempImageSrcs(tempFileNames);
			}

			return "admin/ad/regist_check";

		} catch (IOException e) {
			// ファイルが見つからない等のエラーが発生した場合
			e.printStackTrace();
			// モデルにエラーメッセージを追加
			model.addAttribute("errorMessage", "ファイルのアップロードセッションがタイムアウトしました。お手数ですが、再度ファイルを選択してください。");
			return "admin/ad/regist_input";
		}
	}

	/**
	 * 登録完了処理
	 * @param form 広告情報が格納されたフォーム 
	 * @param result 結果
	 * @param status セッションステータス
	 * @return 完了画面表示処理へのリダイレクトURL
	 * @throws Exception  登録処理で予期せぬ例外が発生した場合
	 */
	@RequestMapping(path = "/complete", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public String complete(@Valid @ModelAttribute("promotionsForm") PromotionsForm form,
			BindingResult result,
			SessionStatus status) throws Exception {

		// DB登録処理
		Promotions entity = promotionConverter.convertToEntity(form);
		entity.setDeleteFlag(0);
		promotionsRepository.save(entity);

		// ファイル移動
		moveFile(form.getTempImageName());
		moveFile(form.getTempHeadingImage());
		if (form.getTempImageSrcs() != null) {
			for (String fileName : form.getTempImageSrcs()) {
				moveFile(fileName);
			}
		}

		// セッションクリア
		status.setComplete();

		// HTMLを返すのではなく、完了画面のURLへ「リダイレクト」させる
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
