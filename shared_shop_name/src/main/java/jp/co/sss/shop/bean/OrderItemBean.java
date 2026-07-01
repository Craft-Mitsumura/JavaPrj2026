package jp.co.sss.shop.bean;

/**
 * 注文商品情報クラス
 *
 * @author SystemShared
 */
public class OrderItemBean {
	/**
	 * 注文商品ID
	 */
	private Integer id;
	/**
	 * 注文商品名
	 */
	private String name;
	/**
	 * 価格
	 */
	private Integer price;
	/**
	 * 商品画像ファイル名
	 */
	private String image;
	/**
	 * 注文個数
	 */
	private Integer orderNum;
	/**
	 * 小計
	 */
	private Integer subtotal;

	/**
	 * 注文商品IDの取得
	 * @return 注文商品ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 注文商品IDのセット
	 * @param id 注文商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文商品名の取得
	 * @return 注文商品名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 注文商品名のセット
	 * @param name 注文商品名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 注文時の商品単価を取得
	 * @return 注文時の商品単価
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * 注文時の商品単価をセット
	 * @param price 注文時の商品単価
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}

	/**
	 * 画像ファイル名を取得
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
	 * 商品ごとの小計額の取得
	 * @return 商品ごとの小計額
	 */
	public Integer getSubtotal() {
		return subtotal;
	}

	/**
	 * 商品ごとの小計額のセット
	 * @param subtotal 商品ごとの小計額
	 */
	public void setSubtotal(Integer subtotal) {
		this.subtotal = subtotal;
	}

	/**
	 * 注文個数の取得
	 * @return 注文個数
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 注文個数のセット
	 * @param orderNum 注文個数
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
}
