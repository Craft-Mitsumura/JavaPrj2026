package jp.co.sss.shop.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 会員情報エンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "users")
public class User {

	/**
	 * 会員ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_users_gen")
	@SequenceGenerator(name = "seq_users_gen", sequenceName = "seq_users", allocationSize = 1)
	private Integer id;

	/**
	 * 会員メールアドレス
	 */
	@Column
	private String email;

	/**
	 * パスワード
	 */
	@Column
	private String password;

	/**
	 * 会員名
	 */
	@Column
	private String name;

	/**
	 * 郵便番号
	 */
	@Column
	private String postalCode;

	/**
	 * 住所
	 */
	@Column
	private String address;

	/**
	 * 電話番号
	 */
	@Column
	private String phoneNumber;

	/**
	 * 権限
	 */
	@Column
	private Integer authority;

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
}