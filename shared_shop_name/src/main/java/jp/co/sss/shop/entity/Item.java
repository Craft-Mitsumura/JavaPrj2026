package jp.co.sss.shop.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 商品情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "items")
public class Item {
	/**
	 * 商品ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_items_gen")
	@SequenceGenerator(name = "seq_items_gen", sequenceName = "seq_items", allocationSize = 1)
	private Integer id;

	/**
	 * 商品名
	 */
	@Column
	private String name;

	/**
	 * 価格
	 */
	@Column
	private Integer price;

	/**
	 * 商品説明
	 */
	@Column
	private String description;

	/**
	 * 在庫数
	 */
	@Column
	private Integer stock;

	/**
	 * 商品画像ファイル名
	 */
	@Column
	private String image;

	/**
	 * 削除フラグ
	 */
	@Column(insertable = false)
	private Integer deleteFlag;

	/**
	 * 登録日付
	 */
	@Column(insertable = false, updatable = false)
	private Date insertDate;

	/**
	 * カテゴリ情報
	 */
	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	/**
	 * 注文商品情報
	 */
	@OneToMany(mappedBy = "item")
	private List<OrderItem> orderItemList;

	/**
	 * コンストラクタ
	 */
	public Item() {
	}

	/**
	 * コンストラクタ
	 * @param id 商品ID
	 * @param name 商品名
	 * @param description 商品説明
	 * @param image 画像ファイル名
	 * @param category_name カテゴリ名
	 */
	public Item(Integer id, String name, String description, String image, String category_name) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
		this.category = new Category();
		this.category.setName(category_name);
	}

	/**
	 * コンストラクタ
	 * @param id 商品ID
	 * @param name 商品名
	 * @param price 価格
	 * @param description 商品説明
	 * @param image 画像ファイル名
	 * @param category_name カテゴリ名
	 */
	public Item(Integer id, String name, Integer price, String description, String image, String category_name) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.description = description;
		this.image = image;
		this.category = new Category();
		this.category.setName(category_name);
	}

	/**
	 * 商品IDの取得
	 * @return 商品ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 商品IDのセット
	 * @param id 商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 商品名の取得
	 * @return 商品名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 商品名のセット
	 * @param name 商品名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 商品単価の取得
	 * @return 商品単価
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * 商品単価のセット
	 * @param price 商品単価
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}

	/**
	 *  商品説明文の取得
	 * @return 商品説明文
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 商品説明文のセット
	 * @param description 商品説明文
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 在庫数の取得
	 * @return 在庫数
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * 在庫数のセット
	 * @param stock 在庫数
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * 画像ファイル名の取得
	 * @return 画像ファイル名
	 */
	public String getImage() {
		return image;
	}

	/**
	 * 画像ファイル名のセット
	 * @param image 画像ファイル名
	 */
	public void setImage(String image) {
		this.image = image;
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
	 * カテゴリエンティティの取得
	 * @return カテゴリエンティティ
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * カテゴリエンティティのセット
	 * @param category カテゴリエンティティ
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * 商品IDに紐づく注文商品エンティティのリストを取得
	 * @return 注文商品エンティティのリスト
	 */
	public List<OrderItem> getOrderItemsList() {
		return orderItemList;
	}

	/**
	 * 商品IDに紐づく注文商品エンティティのリストをセット
	 * @param orderItemList 注文商品エンティティのリスト
	 */
	public void setOrderItemsList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

}
