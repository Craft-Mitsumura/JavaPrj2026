package jp.co.sss.shop.validator;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sss.shop.annotation.ItemCheck;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 商品名の重複登録検証クラス
 *
 * @author System Shared
 */

public class ItemValidator implements ConstraintValidator<ItemCheck, Object> {

	/**
	 * 商品名
	 */
	private String name;

	/**
	 * 商品ID
	 */
	private String id;

	/**
	 * 商品情報レポジトリ
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 初期化処理
	 *
	 * @param annotation
	 *            商品チェックのアノテーション
	 */
	@Override
	public void initialize(ItemCheck annotation) {
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
		Item item_same_name = itemRepository.findByNameAndDeleteFlag(nameProp, Constant.NOT_DELETED);

		if (item_same_name == null) {
			// 同じ商品名の情報が存在していない場合は、有効
			isValidFlg = true;
		} else {
			if (idProp == item_same_name.getId()) {
				// 同じ商品名の商品IDが変更対象のIDと一致する場合は、有効
				isValidFlg = true;

			} else {
				// 名前重複エラー
				isValidFlg = false;
			}
		}
		return isValidFlg;
	}
}
