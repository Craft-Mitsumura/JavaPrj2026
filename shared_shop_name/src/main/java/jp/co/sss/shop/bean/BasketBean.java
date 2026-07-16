package jp.co.sss.shop.bean;

/**
 * 買い物かご内の商品情報クラス
 *
 * @author SystemShared
 */

public class BasketBean {

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 商品名
	 */
	private String name;

	/**
	 * 商品在庫数
	 */
	private Integer stock;

	/**
	 * 商品価格
	 */
	private Integer price;

	/**
	 * 商品注文個数 初期値 1
	 */
	private Integer orderNum = 1;

	// 追加分
	/**
	 * 刻印有効フラグ
	 */
	private boolean isEngravingSelected;

	/**
	 * 刻印文字列
	 */
	private String engravingText;

	/**
	 * 刻印フォント
	 */
	private String fontType;

	/**
	 * 識別ID
	 */
	private Integer engravingId;

	/**
	 * コンストラクタ
	 */
	public BasketBean() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param id  商品ID
	 * @param name  商品名
	 * @param stock 商品在庫数
	 * @param price 金額
	 * 
	 * 追加分：金城
	 * @param isEngravingSelected 刻印有効フラグ
	 * @param engravingText 刻印文字列
	 * @param fontType 	刻印フォント
	 * @param engravingId 	識別ID
	 */
	public BasketBean(Integer id, String name, Integer stock, Integer price, boolean isEngravingSelected,
			String engravingText, String fontType, Integer engravingId) {
		this.id = id;
		this.name = name;
		this.stock = stock;
		this.price = price;

		// 追加分
		this.isEngravingSelected = isEngravingSelected;
		this.engravingText = engravingText;
		this.fontType = fontType;
		this.engravingId = engravingId;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param id  商品ID
	 * @param name  商品名
	 * @param stock  商品在庫数
	 * @param orderNum  注文個数
	 * @param price 金額
	 * 
	 * 追加分：金城
	 * @param isEngravingSelected 刻印有効フラグ
	 * @param engravingText 刻印文字列
	 * @param fontType 	刻印フォント
	 * @param engravingId 	識別ID
	 */
	public BasketBean(Integer id, String name, Integer stock, Integer price, Integer orderNum,
			boolean isEngravingSelected, String engravingText, String fontType, Integer engravingId) {
		this.id = id;
		this.name = name;
		this.stock = stock;
		this.price = price;
		this.orderNum = orderNum;

		// 追加分
		this.isEngravingSelected = isEngravingSelected;
		this.engravingText = engravingText;
		this.fontType = fontType;
		this.engravingId = engravingId;
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
	 * 商品金額
	 * @return
	 */
	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	/**
	 * 商品の在庫数の取得
	 * @return 在庫数
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * 商品の在庫数のセット
	 * @param stock 在庫数
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * 買い物かごに入れている商品個数の取得
	 * @return 注文個数
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 買い物かごに入れる商品個数のセット
	 * @param orderNum 注文個数
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**追加分
	 * 刻印有効フラグ
	 * @return OFF/ON 0/1
	 */
	public boolean isEngravingSelected() {
		return isEngravingSelected;
	}

	/**
	 * 刻印有効フラグ
	 * @param isEngravingSelected 刻印有効フラグ
	 */
	public void setIsEngravingSelected(boolean isEngravingSelected) {
		this.isEngravingSelected = isEngravingSelected;
	}

	/**
	 * 刻印文字列
	 * @return 刻印文字列
	 */
	public String getEngravingText() {
		return engravingText;
	}

	/**
	 * 刻印文字列
	 * @param engravingText 刻印文字列
	 */
	public void setEngravingText(String engravingText) {
		this.engravingText = engravingText;
	}

	/**
	 * 刻印フォント
	 * @return 刻印フォント
	 */
	public String getFontType() {
		return fontType;
	}

	/**
	 * 刻印フォント
	 * @param fontType 刻印フォント
	 */
	public void setFontType(String fontType) {
		this.fontType = fontType;
	}

	/**
	 * 識別ID
	 * @return 識別ID
	 */
	public Integer getEngravingId() {
		return engravingId;
	}

	/**
	 * 識別ID
	 * @param engravingId 識別ID
	 */
	public void setEngravingId(Integer engravingId) {
		this.engravingId = engravingId;
	}
}
