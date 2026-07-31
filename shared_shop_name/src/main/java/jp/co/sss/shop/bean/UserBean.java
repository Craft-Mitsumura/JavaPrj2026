package jp.co.sss.shop.bean;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 会員情報クラス
 *
 * @author SystemShared
 */
public class UserBean {
	
	/**
	 * 会員ID
	 */
	private Integer id;
	
	/**
	 * 会員メールアドレス
	 */
	@NotNull
	@Email
	private String email;
	
	/**
	 * パスワード
	 */
	@NotNull
	@Size(min = 8, max = 16)
	@Pattern(regexp = "^[a-zA-Z0-9]+$")
	private String password;
	
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
	 * 会員メールアドレスの取得
	 * @return 会員メールアドレス
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 会員メールアドレスのセット
	 * @param email 会員メールアドレス
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
 
	/** 
	 * ポイント
	 */
	private Integer point;

	/**
	 * ポイントの取得
	 */
	public Integer getPoint() {
		return point;
	}

	/**
	 * ポイントのセット
	 */
	public void setPoint(Integer point) {
		this.point = point;
	}

}
