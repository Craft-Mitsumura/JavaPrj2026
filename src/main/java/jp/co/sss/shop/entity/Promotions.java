package jp.co.sss.shop.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	 * @author	金城
	 * レイアウトタイプ
	 */
	@Column(name = "layout_type")
	private Integer layoutType;
	
	/**
	 * @author	金城
	 * 設計書文字列
	 */
	@Column(name = "content_json")
	private String contentJson;
	
	/** 
	 * @author	金城
	 * 遷移先URL
	 */
	@Column(name = "target_url")
	private String targetUrl;
	 
	/**
	 * @author	金城
	 * 有効フラグ
	 */
	@Column(name = "is_active", insertable = true)
	private Integer isActive;
	
	/**
	 * @author	金城
	 * 削除フラグ
	 */
	@Column(name = "delete_flag", insertable = true)
	private Integer deleteFlag;
	
	/**
	 * @author	金城
	 * 登録日時
	 */
	@Column(name = "insert_date", insertable = false, updatable = false)
	private LocalDateTime insertDate;
	
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
	public Promotions(Integer id, String imageName, String targetUrl) {
		this.id = id;
		this.imageName = imageName;
		this.targetUrl = targetUrl;
	}
	
	/**
	 * @author	金城
	 * コンストラクタ：広告ページ用
	 * 
	 * @param id 広告ID
	 * @param layoutType レイアウトタイプ
	 * @param contentJson 設計書文字列
	 */
	public Promotions(Integer id, Integer layoutType, String contentJson) {
		this.id = id;
		this.layoutType = layoutType;
		this.contentJson = contentJson;
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
	 * @author	金城
	 * レイアウトタイプの取得
	 * 
	 * @return layoutType
	 */
	public Integer getLayoutType() {
		return layoutType;
	}
	
	/**
	 * @author	金城
	 * レイアウトタイプのセット
	 * 
	 * @param layoutType レイアウトタイプ
	 */
	public void setLayoutType(Integer layoutType) {
		this.layoutType = layoutType;
	}
	
	/**
	 * @author	金城
	 * 設計書文字列の取得
	 * 
	 * @return contentJson
	 */
	public String getContentJson() {
		return contentJson;
	}
	
	/**
	 * @author	金城
	 * 設計書文字列のセット
	 * 
	 * @param contentJson 文字列
	 */
	public void setContentJson(String contentJson) {
		this.contentJson = contentJson;
	}
	
	/**
	 * @author	金城
	 * 遷移先URLの取得
	 * 
	 * @return targetUrl
	 */
	public String getTargetUrl() {
		return targetUrl;
	}
	
	/**
	 * @author	金城
	 * 遷移先URLのセット
	 * 
	 * @param targetUrl 遷移先URL
	 */
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
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
	
	/**
	 * @author	金城
	 * 削除フラグの取得
	 * 
	 * @return deleteFlag
	 */
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	
	/**
	 * @author	金城
	 * 削除フラグのセット
	 * 
	 * @param deleteFlag 削除フラグ
	 */
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	/**
	 * @author	金城
	 * 登録日時の取得
	 * 
	 * @return insertDate
	 */
	public LocalDateTime getInsertDate() {
		return insertDate;
	}
	
	/**
	 * @author	金城
	 * 登録日時のセット
	 * 
	 * @param insertDate 登録日時
	 * 
	 */
	public void setInsertDate(LocalDateTime insertDate) {
		this.insertDate = insertDate;
	}
	
	/**
	 * adTitle取得用
	 * @return 文字列
	 */
	public String getAdTitle() {
	    try {
	        // 文字列の contentJson を解析して、"広告題名" というキーの値を取り出す
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root = mapper.readTree(this.contentJson);
	        return root.get("広告題名").asText();
	    } catch (Exception e) {
	        return "取得失敗";
	    }
	}
	
}
