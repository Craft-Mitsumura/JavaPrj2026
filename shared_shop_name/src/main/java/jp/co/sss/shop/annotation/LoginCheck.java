package jp.co.sss.shop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import jp.co.sss.shop.validator.LoginValidator;

/**
 * ログインチェックの独自アノテーション定義
 *
 * @author System Shared
 */

/**
 * アノテーション付与対象
 */
@Target({ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.ANNOTATION_TYPE })

/**
 * アノテーション情報の維持範囲
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented

/**
 * 入力チェック対象：LoginValidator
 */
@Constraint(validatedBy = { LoginValidator.class })
public @interface LoginCheck {
	/**
	 * 入力チェックNGの場合のメッセージを設定
	 * @return 表示メッセージ
	 */
	String message() default "{login.missing.message}";

	/**
	 * 特定のバリデーショングループを設定(設定なし)
	 * @return バリデーショングループのクラスリスト
	 */
	Class<?>[] groups() default {};

	/**
	 * 検証対象データに対する属性や関連する情報を定義  (処理定義なし)
	 * @return 対象となるオブジェクトのペイロード
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * メールアドレスの取得
	 * @return メールアドレス
	 */
	String fieldEmail() default "email";

	/**
	 * パスワードの取得
	 * @return パスワード
	 */
	String fieldPassword() default "password";
}
