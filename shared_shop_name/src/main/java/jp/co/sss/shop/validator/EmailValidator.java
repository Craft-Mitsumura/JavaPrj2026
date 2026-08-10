package jp.co.sss.shop.validator;

import java.util.Objects;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sss.shop.annotation.EmailCheck;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

/**
 * メールアドレス重複チェックの独自検証クラス
 *
 * @author System Shared
 */
public class EmailValidator implements ConstraintValidator<EmailCheck, Object> {
	private String emailField;
	private String idField;
	private String authorityField;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Override
	public void initialize(EmailCheck annotation) {
		this.emailField = annotation.fieldEmail();
		this.idField = annotation.fieldId();
		this.authorityField = annotation.fieldAuthority();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);

		String emailProp = (String) beanWrapper.getPropertyValue(this.emailField);
		Integer idProp = (Integer) beanWrapper.getPropertyValue(this.idField);
		Integer authorityProp = (Integer) beanWrapper.getPropertyValue(this.authorityField);

		// emailまたはauthorityが未入力の場合はスキップ（他のバリデーションに委ねる）
		if (emailProp == null || emailProp.isBlank() || authorityProp == null) {
			return true;
		}

		// email + authority の組み合わせで重複チェック
		Optional<User> existingUser = userRepository.findByEmailAndAuthority(emailProp, authorityProp);

		if (!existingUser.isPresent()) {
			// 同じemail+authorityのユーザが存在しない → 有効
			return true;
		}

		// 同じemail+authorityのユーザが存在する場合、自分自身かチェック
		// （更新時：idPropが一致すれば自分のレコード → 有効）
		return Objects.equals(existingUser.get().getId(), idProp);
	}

}

