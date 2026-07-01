package jp.co.sss.shop.validator;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sss.shop.annotation.CategoryCheck;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.util.Constant;

/**
 * カテゴリの重複登録検証クラス
 *
 * @author System Shared
 */

public class CategoryValidator implements ConstraintValidator<CategoryCheck, Object> {

	/**
	 * カテゴリ名
	 */
	private String name;

	/**
	 * カテゴリID
	 */
	private String id;

	/**
	 * カテゴリ情報レポジトリ
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 初期化処理
	 *
	 * @param annotation
	 *            カテゴリチェックのアノテーション
	 */
	@Override
	public void initialize(CategoryCheck annotation) {
		this.name = annotation.fieldName();
		this.id = annotation.fieldId();
	}

	/**
	 * 入力値チェック処理
	 *
	 * @param value
	 *            チェック対象
	 * @param context
	 *            バリデーションコンテキスト
	 * @return true:エラーなし false:エラーあり
	 * @see jakarta.validation.ConstraintValidator
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		boolean isValidFlg = false;
		String nameProp = (String) beanWrapper.getPropertyValue(this.name);
		Integer idProp = (Integer) beanWrapper.getPropertyValue(this.id);
		Category category_same_name = categoryRepository.findByNameAndDeleteFlag(nameProp, Constant.NOT_DELETED);

		if (category_same_name == null) {
			// 同じカテゴリ名の情報が存在していない場合は、有効
			isValidFlg=true;
		}else if (idProp == category_same_name.getId()) {
			// 同じカテゴリ名のカテゴリIDが変更対象のIDと一致する場合は、有効
			isValidFlg=true;
		}else {
			// 名前重複エラー
			isValidFlg=false;

		}
		return isValidFlg;

	}
}
