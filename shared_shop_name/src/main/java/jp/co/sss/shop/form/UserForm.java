package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import jp.co.sss.shop.annotation.EmailCheck;

/**
 * 会員情報入力フォーム
 *
 * @author SystemShared
 * 
 *  TIPS 入力チェックアノテーションのmessage属性に"{messages.propertiesで指定した名前}"と記述することができます。
 */
@EmailCheck
public class UserForm implements Serializable {
	/**
	 * 会員ID
	 */
	private Integer	id;

	/**
	 * 会員メールアドレス
	 */
	@NotBlank
	@Email
	private String	email;

	/**
	 * パスワード
	 */
	@NotBlank
	@Size(min = 8, max = 16)
	@Pattern(regexp = "^[a-zA-Z0-9]+$")
	private String	password;

	/**
	 * 会員名
	 */
	@NotBlank
	@Size(min = 1, max = 30, message = "{text.maxsize.message}")
	private String	name;

	/**
	 * 郵便番号
	 */
	@NotBlank
	@Size(min = 7, max = 7, message = "{text.fixsize.message}")
	@Pattern(regexp = "^[0-9]+$", message = "{userRegist.numberpattern.message}")
	private String	postalCode;

	/**
	 * 住所
	 */
	@NotBlank
	@Size(min = 1, max = 150, message = "{text.maxsize.message}")
	private String	address;

	/**
	 * 電話番号
	 */
	@NotBlank
	@Size(min = 10, max = 11)
	@Pattern(regexp = "^[0-9]+$", message = "{userRegist.numberpattern.message}")
	private String	phoneNumber;

	/**
	 * 権限
	 */
	private Integer authority;

	
	/**
	 * 会員IDの取得
	 * @return 会員ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 会員IDのセット
	 * @param id 会員ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * メールアドレスの取得
	 * @return メールアドレス
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * メールアドレスのセット
	 * @param email メールアドレス
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * パスワードの取得
	 * @return パスワード
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * パスワードのセット
	 * @param password パスワード
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 会員氏名の取得
	 * @return 会員氏名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 会員氏名のセット
	 * @param name 会員氏名
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 郵便番号の取得
	 * @return 郵便番号
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * 郵便番号のセット
	 * @param postalCode 郵便番号
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * 住所の取得
	 * @return 住所
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 住所のセット
	 * @param address 住所
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 電話番号の取得
	 * @return 電話番号
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * 電話番号のセット
	 * @param phoneNumber 電話番号
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * 権限の取得
	 * @return 権限
	 */
	public Integer getAuthority() {
		return authority;
	}

	/**
	 * 権限のセット
	 * @param authority 権限
	 */
	public void setAuthority(Integer authority) {
		this.authority = authority;
	}


}
