package jp.co.sss.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * @author	金城（チームF）
 * 広告情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "promotions")
public class Promotions {
	/**
	 * @author	金城
	 * 広告ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_promotions_gen")
	@SequenceGenerator(name = "seq_promotions_gen", sequenceName = "seq_promotions", allocationSize = 1)
	private Integer id;

	/**
	 * @author	金城
	 * カルーセル画像名
	 */
	@Column(name = "image_name")
	private String imageName;

	/**
	 * @author 金城
	 * 管理名
	 */
	@Column(name = "page_name")
	private String pageName;

	/** 
	 * @author	金城
	 * 遷移先URL
	 */
	@ManyToOne
	@JoinColumn(name = "target_url", nullable = false)
	private Category category;

	/**
	 * @author	金城
	 * 有効フラグ
	 */
	@Column(name = "is_active", insertable = true)
	private Integer isActive = 1;

	/**
	 * @author	金城
	 * コンストラクタ
	 */
	public Promotions() {
	}

	/**
	 * @author	金城
	 * コンストラクタ：トップ画面用
	 * 
	 * @param id 広告ID
	 * @param imageName カルーセル画像名
	 * @param targetUrl 遷移先URL
	 */
	public Promotions(Integer id, String imageName, Category category) {
		this.id = id;
		this.imageName = imageName;
		this.category = category;
	}

	//--------ゲッターセッター-------------

	/**
	 * @author	金城
	 * 広告IDの取得
	 * 
	 * @return 広告ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @author	金城
	 * 広告IDのセット
	 * 
	 * @param id 広告ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @author	金城
	 * カルーセル画像名の取得
	 * 
	 * @return imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @author	金城
	 * カルーセル画像名のセット
	 * 
	 * @param imageName カルーセル画像名
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @author 金城
	 * 管理名の取得
	 * 
	 * @return pageName
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * @author 金城
	 * 管理名のセット
	 * 
	 * @param pageName
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	/**
	 * @author	金城
	 * 遷移先カテゴリのURLの取得
	 * 
	 * @return category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @author	金城
	 * 遷移先カテゴリのURLのセット
	 * 
	 * @param category 遷移先カテゴリのURL
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * @author	金城
	 * 有効フラグの取得
	 * 
	 * @return isActive
	 */
	public Integer getIsActive() {
		return isActive;
	}

	/**
	 * @author	金城
	 * 有効フラグのセット
	 * 
	 * @param isActive アクティブ化
	 */
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

}
