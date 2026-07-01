package jp.co.sss.shop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import jp.co.sss.shop.validator.EmailValidator;

/**
 * Email重複チェックの独自アノテーション定義
 *
 * @author SystemShared
 */

/**
 * アノテーション付与対象
 */
@Target({ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.ANNOTATION_TYPE,
		java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD,
		java.lang.annotation.ElementType.PARAMETER })

/**
 * アノテーション情報の維持範囲
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented

/**
 * 入力チェック対象：EmailValidator
 */
@Constraint(validatedBy = { EmailValidator.class })
public @interface EmailCheck {
	/**
	 * 入力チェックNGの場合のメッセージを設定
	 * @return 表示メッセージ
	 */
	String message() default "{userRegist.duplicate.message}";

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
	 * ユーザIDの取得
	 * @return ユーザID
	 */
	String fieldId() default "id";

}
