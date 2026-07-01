package jp.co.sss.shop.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * カテゴリ情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "categories")
public class Category {

	/**
	 * カテゴリID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categories_gen")
	@SequenceGenerator(name = "seq_categories_gen", sequenceName = "seq_categories", allocationSize = 1)
	private Integer id;

	/**
	 * カテゴリ名
	 */
	@Column
	private String name;

	/**
	 * カテゴリ説明
	 */
	@Column
	private String description;

	/**
	 * 削除フラグ 0:未削除、1:削除済み
	 */
	@Column(insertable = false)
	private Integer deleteFlag;

	/**
	 * 登録日付
	 */
	@Column(insertable = false)
	private Date insertDate;

	/**
	 * 商品リスト
	 */
	@OneToMany(mappedBy = "category")
	private List<Item> itemList;

	/**
	 * コンストラクタ
	 */
	public Category() {
	}

	/**
	 * コンストラクタ
	 * @param name カテゴリ名
	 */
	public Category(String name) {
		this.name = name;
	}

	/**
	 * カテゴリIDの取得
	 * @return カテゴリID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * カテゴリIDのセット
	 * @param id カテゴリID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * カテゴリ名の取得
	 * @return カテゴリ名
	 */
	public String getName() {
		return name;
	}

	/**
	 * カテゴリ名のセット
	 * @param name カテゴリ名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * カテゴリ説明文の取得
	 * @return カテゴリ説明文
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * カテゴリ説明文のセット 
	 * @param description カテゴリ説明文
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 削除フラグの取得
	 * @return 削除フラグ
	 */
	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * 削除フラグのセット
	 * @param deleteFlag 削除フラグ
	 */
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public Date getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日付のセット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * カテゴリIDに紐づく商品エンティティのリストの取得
	 * @return 商品エンティティのリスト
	 */
	public List<Item> getItemList() {
		return itemList;
	}

	/**
	 * カテゴリIDに紐づく商品エンティティのリストのセット
	 * @param itemList 商品エンティティのリスト
	 */
	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
}
