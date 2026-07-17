package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import jp.co.sss.shop.annotation.LoginCheck;

/**
 * ログインのフォーム
 *
 * @author SystemShared
 */
@LoginCheck
public class LoginForm implements Serializable {

	/**
	 * メールアドレス
	 */
	@NotBlank
	@Email
	private String email;

	/**
	 * パスワード
	 */
	@NotBlank
	@Size(min = 8, max = 16)
	@Pattern(regexp = "^[a-zA-Z0-9]+$")
	private String password;

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
}
