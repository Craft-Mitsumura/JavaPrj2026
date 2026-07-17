package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 注文情報入力フォーム
 *
 * @author SystemShared
 *
 * TIPS 入力チェックアノテーションのmessage属性に"{messages.propertiesで指定した名前}"と記述することができます。
 * 
 */
public class OrderForm implements Serializable {

	/**
	 * 会員ID
	 */
	private Integer id;

	/**
	 * 送付先郵便番号
	 */
	@NotBlank
	@Size(min = 7, max = 7, message = "{text.fixsize.message}")
	@Pattern(regexp = "^[0-9]+$", message = "{userRegist.numberpattern.message}")
	private String postalCode;

	/**
	 * 送付先住所
	 */
	@NotBlank
	@Size(min = 1, max = 150, message = "{text.maxsize.message}")
	private String address;

	/**
	 * 送付先氏名
	 */
	@NotBlank
	@Size(min = 1, max = 30, message = "{text.maxsize.message}")
	private String name;

	/**
	 * 送付先電話番号
	 */
	@NotBlank
	@Size(min = 10, max = 11)
	@Pattern(regexp = "^[0-9]+$", message = "{userRegist.numberpattern.message}")
	private String phoneNumber;

	/**
	 * 支払い方法
	 */
	private Integer payMethod;

	/**
	 * 注文するユーザIDの取得
	 * @return 注文するユーザID
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


}
