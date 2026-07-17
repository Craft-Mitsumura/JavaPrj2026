package jp.co.sss.shop.form;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

/**
 * 広告情報のフォーム
 * * @author 金城（チームF）
 */
public class PromotionsForm implements Serializable {
	/**
	 * 広告ID
	 */
	private Integer id;

	/**
	 * カルーセル画像
	 */
	@NotNull(message = "サムネイルを選択してください")
	private MultipartFile imageName;

	/**
	 * レイアウトタイプ
	 */
	private Integer layoutType;

	/**
	 * 遷移先URL
	 */
	@NotBlank(message = "遷移先URLは必須です")
	@Size(max = 225, message = "遷移先URLは225文字以内で入力してください")
	private String targetUrl;

	/**
	 * 有効フラグ初期値1
	 */
	private Integer isActive = 1;

	/**
	 * ページ名（設計書文字列 content_json 内の項目）
	 */
	@NotBlank(message = "ページ名は必須です")
	@Size(max = 100, message = "ページ名は100文字以内で入力してください")
	private String pageName;

	/**
	 * 見出し（設計書文字列 content_json 内の項目）
	 */
	@NotBlank(message = "見出しは必須です")
	@Size(max = 100, message = "見出しは100文字以内で入力してください")
	private String adTitle;

	/**
	 * タイトル画像（設計書文字列 content_json 内の項目）
	 */
	@NotNull(message = "タイトル画像を選択してください")
	private MultipartFile headingImage;

	/**
	 * 本文（設計書文字列 content_json 内の項目）
	 */
	@Size(max = 1000, message = "本文は1000文字以内で入力してください")
	private String body;

	/**
	 * 画像ソースリスト
	 */
	private List<MultipartFile> imageSrcs;

	/**
	 * 画像説明テキストリスト
	 */
	private List<String> imageTexts;

	/**
	 * 画像alt属性リスト
	 */
	private List<String> imageAlts;

	/**
	 * リンクの表示文字リスト
	 */
	private List<String> linkTexts;

	/**
	 * リンクURLリスト
	 */
	private List<String> linkUrls;

	/**
	 * 一時保存用のサムネイルファイル名
	 */
	private String tempImageName;

	/**
	 * 一時保存用のタイトル画像ファイル名
	 */
	private String tempHeadingImage;

	/**
	 * 一時保存用の画像リストファイル名
	 */
	private List<String> tempImageSrcs;

	/**
	 * 一時保存用のサムネイルファイル名を取得します。
	 * @return 一時保存用のサムネイルファイル名
	 */
	public String getTempImageName() {
		return tempImageName;
	}

	/**
	 * 一時保存用のサムネイルファイル名を設定します。
	 * @param tempImageName 一時保存用のサムネイルファイル名
	 */
	public void setTempImageName(String tempImageName) {
		this.tempImageName = tempImageName;
	}

	/**
	 * 一時保存用のタイトル画像ファイル名を取得します。
	 * @return 一時保存用のタイトル画像ファイル名
	 */
	public String getTempHeadingImage() {
		return tempHeadingImage;
	}

	/**
	 * 一時保存用のタイトル画像ファイル名を設定します。
	 * @param tempHeadingImage 一時保存用のタイトル画像ファイル名
	 */
	public void setTempHeadingImage(String tempHeadingImage) {
		this.tempHeadingImage = tempHeadingImage;
	}

	/**
	 * 一時保存用の画像リストファイル名を取得します。
	 * @return 一時保存用の画像リストファイル名
	 */
	public List<String> getTempImageSrcs() {
		return tempImageSrcs;
	}

	/**
	 * 一時保存用の画像リストファイル名を設定します。
	 * @param tempImageSrcs 一時保存用の画像リストファイル名
	 */
	public void setTempImageSrcs(List<String> tempImageSrcs) {
		this.tempImageSrcs = tempImageSrcs;
	}

	/**
	 * 初期化コンストラクタです。各リストのインスタンスを生成します。
	 */
	public PromotionsForm() {
		this.imageSrcs = new java.util.ArrayList<>();
		this.imageTexts = new java.util.ArrayList<>();
		this.imageAlts = new java.util.ArrayList<>();
		this.linkTexts = new java.util.ArrayList<>();
		this.linkUrls = new java.util.ArrayList<>();
	}

	/**
	 * サムネイル画像が選択されているか、または既に一時保存されているかを検証します。
	 * @return 検証結果（有効な場合はtrue）
	 */
	@AssertTrue(message = "サムネイルを選択してください")
	public boolean isImageNameValid() {
		return (this.tempImageName != null && !this.tempImageName.isEmpty())
				|| (this.imageName != null && !this.imageName.isEmpty());
	}

	/**
	 * タイトル画像が選択されているか、または既に一時保存されているかを検証します。
	 * @return 検証結果（有効な場合はtrue）
	 */
	@AssertTrue(message = "タイトル画像を選択してください")
	public boolean isHeadingImageValid() {
		return (this.tempHeadingImage != null && !this.tempHeadingImage.isEmpty())
				|| (this.headingImage != null && !this.headingImage.isEmpty());
	}

	/**
	 * 画像リストが少なくとも1つ選択されているか、または一時保存されているかを検証します。
	 * @return 検証結果（有効な場合はtrue）
	 */
	@AssertTrue(message = "画像リストを少なくとも1つ選択してください")
	public boolean isImageSrcsValid() {
		boolean hasTemp = (this.tempImageSrcs != null && !this.tempImageSrcs.isEmpty());
		boolean hasNew = (this.imageSrcs != null && this.imageSrcs.stream().anyMatch(f -> f != null && !f.isEmpty()));
		return hasTemp || hasNew;
	}

	/**
	 * 広告IDの取得
	 * @return 広告ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 広告IDのセット
	 * @param id 広告ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * カルーセル画像の取得
	 * @return カルーセル画像
	 */
	public MultipartFile getImageName() {
		return imageName;
	}

	/**
	 * カルーセル画像のセット
	 * @param imageName カルーセル画像
	 */
	public void setImageName(MultipartFile imageName) {
		this.imageName = imageName;
	}

	/**
	 * レイアウトタイプの取得
	 * @return レイアウトタイプ
	 */
	public Integer getLayoutType() {
		return layoutType;
	}

	/**
	 * レイアウトタイプのセット
	 * @param layoutType レイアウトタイプ
	 */
	public void setLayoutType(Integer layoutType) {
		this.layoutType = layoutType;
	}

	/**
	 * 遷移先URLの取得
	 * @return 遷移先URL
	 */
	public String getTargetUrl() {
		return targetUrl;
	}

	/**
	 * 遷移先URLのセット
	 * @param targetUrl 遷移先URL
	 */
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	/**
	 * 有効フラグの取得
	 * @return 有効フラグ
	 */
	public Integer getIsActive() {
		return isActive;
	}

	/**
	 * 有効フラグのセット
	 * @param isActive 有効フラグ
	 */
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	/**
	 * ページ名の取得
	 * @return ページ名
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * ページ名のセット
	 * @param pageName ページ名
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	/**
	 * 広告題名の取得
	 * @return 広告題名
	 */
	public String getAdTitle() {
		return adTitle;
	}

	/**
	 * 広告題名のセット
	 * @param adTitle 広告題名
	 */
	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	/**
	 * タイトル画像の取得
	 * @return タイトル画像
	 */
	public MultipartFile getHeadingImage() {
		return headingImage;
	}

	/**
	 * タイトル画像のセット
	 * @param headingImage タイトル画像
	 */
	public void setHeadingImage(MultipartFile headingImage) {
		this.headingImage = headingImage;
	}

	/**
	 * 本文の取得
	 * @return 本文
	 */
	public String getBody() {
		return body;
	}

	/**
	 * 本文のセット
	 * @param body 本文
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * 画像説明テキストの取得
	 * @return 画像説明テキスト
	 */
	public List<String> getImageTexts() {
		return imageTexts;
	}

	/**
	 * 画像説明テキストのセット
	 * @param imageTexts 画像説明テキスト
	 */
	public void setImageTexts(List<String> imageTexts) {
		this.imageTexts = imageTexts;
	}

	/**
	 * 画像ソースの取得
	 * @return 画像ソース
	 */
	public List<MultipartFile> getImageSrcs() {
		return imageSrcs;
	}

	/**
	 * 画像ソースのセット
	 * @param imageSrcs 画像ソース
	 */
	public void setImageSrcs(List<MultipartFile> imageSrcs) {
		this.imageSrcs = imageSrcs;
	}

	/**
	 * 画像altの取得
	 * @return 画像alt
	 */
	public List<String> getImageAlts() {
		return imageAlts;
	}

	/**
	 * 画像altのセット
	 * @param imageAlts 画像alt
	 */
	public void setImageAlts(List<String> imageAlts) {
		this.imageAlts = imageAlts;
	}

	/**
	 * 表示文字の取得
	 * @return 表示文字
	 */
	public List<String> getLinkTexts() {
		return linkTexts;
	}

	/**
	 * 表示文字のセット
	 * @param linkTexts 表示文字
	 */
	public void setLinkTexts(List<String> linkTexts) {
		this.linkTexts = linkTexts;
	}

	/**
	 * リンクURLの取得
	 * @return リンクURL
	 */
	public List<String> getLinkUrls() {
		return linkUrls;
	}

	/**
	 * リンクURLのセット
	 * @param linkUrls リンクURL
	 */
	public void setLinkUrls(List<String> linkUrls) {
		this.linkUrls = linkUrls;
	}

}