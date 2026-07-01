package jp.co.sss.shop.bean;

/**
 * 商品情報クラス
 *
 * @author SystemShared
 */
public class ItemBean {

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 商品名
	 */
	private String name;

	/**
	 * 価格
	 */
	private Integer price;

	/**
	 * 商品説明
	 */
	private String description;

	/**
	 * 在庫数
	 */
	private Integer stock;

	/**
	 * 商品画像ファイル名
	 */
	private String image;

	/**
	 * カテゴリID
	 */
	private Integer categoryId;

	/**
	 * カテゴリ名
	 */
	private String categoryName;

	/**
	 * 商品ID取得
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
	 * 商品の説明文の取得
	 * @return 商品の説明文
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 商品の説明文のセット
	 * @param description 商品の説明文
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
	 * カテゴリIDの取得
	 * @return カテゴリID
	 */
	public Integer getCategoryId() {
		return categoryId;
	}

	/**
	 * カテゴリIDのセット
	 * @param categoryId カテゴリID
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * カテゴリ名の取得
	 * @return カテゴリ名
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * カテゴリ名のセット
	 * @param categoryName カテゴリ名
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
