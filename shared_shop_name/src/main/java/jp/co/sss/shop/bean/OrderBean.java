package jp.co.sss.shop.bean;

/**
 * 注文情報クラス
 *
 * @author SystemShared
 */
public class OrderBean {

	/**
	 * 注文ID
	 */
	private Integer id;

	/**
	 * 注文日付
	 */
	private String insertDate;

	/**
	 * 支払方法
	 */
	private Integer payMethod;

	/**
	 * 合計金額
	 */
	private Integer total;

	/**
	 * お届け先郵便番号
	 */
	private String postalCode;

	/**
	 * お届け先住所
	 */
	private String address;

	/**
	 * お届け先宛名
	 */
	private String name;

	/**
	 * お届け先電話番号
	 */
	private String phoneNumber;

	/**
	 * 注文会員名
	 */
	private String userName;

	/**
	 * 注文IDの取得
	 * @return 注文ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 注文IDのセット
	 * @param id 注文ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文日付の取得
	 * @return 注文日付
	 */
	public String getInsertDate() {
		return insertDate;
	}

	/**
	 * 注文日付のセット
	 * @param insertDate 注文日付
	 */
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * 支払い方法の取得
	 * @return 支払い方法
	 */
	public Integer getPayMethod() {
		return payMethod;
	}

	/**
	 * 支払い方法のセット
	 * @param payMethod 支払い方法
	 */
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}

	/**
	 * 合計金額の取得
	 * @return 合計金額
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * 合計金額のセット
	 * @param total 合計金額
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}

	/**
	 * お届け先郵便番号の取得
	 * @return お届け先郵便番号
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * お届け先郵便番号のセット
	 * @param postalCode お届け先郵便番号
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * お届け先住所の取得
	 * @return お届け先住所
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * お届け先住所のセット
	 * @param address お届け先住所
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * お届け先氏名の取得
	 * @return お届け先氏名
	 */
	public String getName() {
		return name;
	}

	/**
	 * お届け先氏名のセット
	 * @param name お届け先氏名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * お届け先電話番号の取得
	 * @return お届け先電話番号
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * お届け先電話番号のセット
	 * @param phoneNumber お届け先電話番号
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * 注文会員名の取得
	 * @return 注文会員名
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 注文会員名のセット
	 * @param userName 注文会員名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
