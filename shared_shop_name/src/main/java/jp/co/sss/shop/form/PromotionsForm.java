package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

/**
 * カルーセル広告情報のフォーム
 * @author 金城（チームF）
 */
public class PromotionsForm implements Serializable {
	/**
	 * 広告ID
	 */
	private Integer id;

	/**
	 * カルーセル画像
	 */
	private MultipartFile imageName;

	/**
	 * 遷移先カテゴリID（categoriesテーブルのid）
	 */
	@NotNull(message = "遷移先カテゴリを選択してください。")
	private Integer categoryId;

	/**
	 * 有効フラグ初期値1
	 */
	private Integer isActive = 1;

	/**
	 * 管理画面での表記名（管理名）
	 */
	@NotBlank(message = "管理名は必須です。")
	@Size(max = 100, message = "管理名は100文字以内で入力してください。")
	private String pageName;

	/**
	 * 一時保存用のサムネイルファイル名
	 */
	private String tempImageName;

	/**
	 * デフォルトコンストラクタ
	 */
	public PromotionsForm() {
	}

	/**
	 * サムネイル画像が選択されているか、または既に一時保存されているかを検証します。
	 * @return 検証結果（有効な場合はtrue）
	 */
	@AssertTrue(message = "画像を選択してください。")
	public boolean isImageNameValid() {
		return (this.tempImageName != null && !this.tempImageName.isEmpty())
				|| (this.imageName != null && !this.imageName.isEmpty());
	}

	// --- ゲッター・セッター ---

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MultipartFile getImageName() {
		return imageName;
	}

	public void setImageName(MultipartFile imageName) {
		this.imageName = imageName;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getTempImageName() {
		return tempImageName;
	}

	public void setTempImageName(String tempImageName) {
		this.tempImageName = tempImageName;
	}
}