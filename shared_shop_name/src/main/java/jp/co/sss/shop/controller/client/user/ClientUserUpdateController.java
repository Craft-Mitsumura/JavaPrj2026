package jp.co.sss.shop.controller.client.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員情報変更のコントローラクラス
 *
 * @author 手塚 / 金城
 */
@Controller
public class ClientUserUpdateController {

	@Autowired
	UserRepository userRepository;

	/**
	 * 会員情報変更画面 初期化処理(POST)
	 * @param session セッションスコープ
	 * @return "redirect:/client/user/update/input" 変更入力画面へのリダイレクト
	 */
	@RequestMapping(path = "/client/user/update/input/init", method = RequestMethod.POST)
	public String updateInputInit(HttpSession session) {

		session.removeAttribute("updateUser");
		return "redirect:/client/user/update/input";
	}

	/**
	 * 会員情報変更画面
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/update_input 会員情報編集画面を表示
	 */
	@RequestMapping("/client/user/update/input")
	public String updateInput(@ModelAttribute("userForm") UserBean userBean, Model model, HttpSession session) {

		UserBean updateUser = (UserBean) session.getAttribute("updateUser");
		UserBean pastUser = (UserBean) session.getAttribute("pastUser");

		UserBean targetUser = (updateUser != null) ? updateUser : pastUser;

		userBean.setEmail(targetUser.getEmail());
		userBean.setPassword(targetUser.getPassword());
		userBean.setName(targetUser.getName());
		userBean.setPostalCode(targetUser.getPostalCode());
		userBean.setAddress(targetUser.getAddress());
		userBean.setPhoneNumber(targetUser.getPhoneNumber());
		userBean.setId(targetUser.getId());
		userBean.setAuthority(targetUser.getAuthority());
		userBean.setPoint(targetUser.getPoint());

		if (!model.containsAttribute("oldEmail")) {
			model.addAttribute("oldEmail", pastUser.getEmail());
		}
		if (!model.containsAttribute("newEmail")) {
			String newEmailValue = (updateUser != null) ? updateUser.getEmail() : "";
			model.addAttribute("newEmail", newEmailValue);
		}

		return "client/user/update_input";
	}

	/**
	 * 会員情報編集確認画面
	 * @param userBean 入力された会員情報
	 * @param result 入力チェックの結果を格納するオブジェクト
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/update_check 会員情報編集確認画面を表示
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateCheck(
			@Valid @ModelAttribute("userForm") UserBean userBean,
			BindingResult result,
			@RequestParam(value = "oldEmail", required = false) String oldEmail,
			@RequestParam(value = "oldPassword", required = false) String oldPassword,
			@RequestParam(value = "newEmail", required = true) String newEmail,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestParam(value = "postalCode", required = true) String postalCode,
			@RequestParam(value = "name", required = true) String name,
			Model model,
			HttpSession session) {

		UserBean pastUser = (UserBean) session.getAttribute("pastUser");
		boolean hasError = false;

		// 旧メールアドレスの必須・一致チェック
		if (oldEmail == null || oldEmail.isBlank()) {
			model.addAttribute("oldEmailErrorMessage", "メールアドレスは必須項目です。");
			hasError = true;
		} else if (!oldEmail.equals(pastUser.getEmail())) {
			model.addAttribute("oldEmailErrorMessage", "登録されているメールアドレスと一致しません。");
			hasError = true;
		}

		// 旧パスワードの必須・一致チェック
		if (oldPassword == null || oldPassword.isBlank()) {
			model.addAttribute("oldPasswordErrorMessage", "パスワードは必須項目です。");
			hasError = true;
		} else if (!oldPassword.equals(pastUser.getPassword())) {
			model.addAttribute("oldPasswordErrorMessage", "登録されているパスワードと一致しません。");
			hasError = true;
		}

		// 新しいメールアドレスの形式・重複チェック
		if (newEmail != null && !newEmail.isBlank()) {
			if (!newEmail.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
				model.addAttribute("newEmailErrorMessage", "メールアドレスの形式が正しくありません。");
				hasError = true;
			}

			if (!hasError && !newEmail.equals(pastUser.getEmail())) {
				User duplicateUser = userRepository.findByEmailAndDeleteFlag(newEmail, 0);
				if (duplicateUser != null) {
					model.addAttribute("authErrorMessage", "入力された新しいメールアドレスは既に登録されています。");
					hasError = true;
				}
			}
			userBean.setEmail(newEmail);
		} else {
			userBean.setEmail(pastUser.getEmail());
		}

		// 新しいパスワードのチェック
		if (newPassword != null && !newPassword.isBlank()) {
			if (newPassword.length() < 8 || newPassword.length() > 16) {
				model.addAttribute("newPasswordErrorMessage", "パスワードは8～16文字で入力してください。");
				hasError = true;
			} else if (!newPassword.matches("^[a-zA-Z0-9]+$")) {
				model.addAttribute("newPasswordErrorMessage", "パスワードは正しい形式で入力してください。");
				hasError = true;
			}
			userBean.setPassword(newPassword);
		} else {
			userBean.setPassword(pastUser.getPassword());
		}

		// 氏名
		if (name == null || name.isBlank()) {
			model.addAttribute("nameErrorMessage", "氏名は必須項目です。");
			hasError = true;
		} else if (name.length() > 30) {
			model.addAttribute("nameErrorMessage", "氏名は30文字以内で入力してください。");
			hasError = true;
		} else {
			userBean.setName(name);
		}

		// 郵便番号
		if (postalCode == null || postalCode.isBlank()) {
			model.addAttribute("postalCodeErrorMessage", "郵便番号は必須項目です。");
			hasError = true;
		} else if (!postalCode.matches("^[0-9]+$")) {
			model.addAttribute("postalCodeErrorMessage", "郵便番号は半角数字で入力してください。");
			hasError = true;
		} else if (postalCode.length() != 7) {
			model.addAttribute("postalCodeErrorMessage", "郵便番号は7文字で入力してください。");
			hasError = true;
		} else {
			userBean.setPostalCode(postalCode);
		}

		// 住所
		if (userBean.getAddress() == null || userBean.getAddress().isBlank()) {
			model.addAttribute("addressErrorMessage", "住所は必須項目です。");
			hasError = true;
		} else if (userBean.getAddress().length() > 150) {
			model.addAttribute("addressErrorMessage", "住所は150文字以内で入力してください。");
			hasError = true;
		}

		// 電話番号
		if (userBean.getPhoneNumber() == null || userBean.getPhoneNumber().isBlank()) {
			model.addAttribute("phoneNumberErrorMessage", "電話番号は必須項目です。");
			hasError = true;
		} else if (!userBean.getPhoneNumber().matches("^[0-9]+$")) {
			model.addAttribute("phoneNumberErrorMessage", "電話番号は半角数字で入力してください。");
			hasError = true;
		} else if (userBean.getPhoneNumber().length() < 10 || userBean.getPhoneNumber().length() > 11) {
			model.addAttribute("phoneNumberErrorMessage", "電話番号は10文字以上11文字以内で入力してください。");
			hasError = true;
		}

		// エラーがある場合は、入力値とエラーメッセージを保持して入力画面に戻す
		if (hasError) {
			model.addAttribute("oldEmail", oldEmail);
			model.addAttribute("newEmail", newEmail);
			model.addAttribute("userForm", userBean);
			return "client/user/update_input";
		}

		session.setAttribute("updateUser", userBean);
		model.addAttribute("userForm", userBean);

		return "client/user/update_check";
	}

	/**
	 * 会員情報編集完了処理
	 * @param session セッションスコープ
	 * @return 会員情報編集完了画面表示へリダイレクト
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete(HttpSession session) {

		UserBean userBean = (UserBean) session.getAttribute("updateUser");

		if (userBean != null) {
			User user = userRepository.findById(userBean.getId()).orElse(null);

			if (user != null) {
				user.setEmail(userBean.getEmail());
				user.setPassword(userBean.getPassword());
				user.setName(userBean.getName());
				user.setPostalCode(userBean.getPostalCode());
				user.setAddress(userBean.getAddress());
				user.setPhoneNumber(userBean.getPhoneNumber());

				userRepository.save(user);
				session.setAttribute("user", userBean);
			}
		}

		session.removeAttribute("updateUser");
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 会員情報編集完了画面表示
	 * @return client/user/update_complete 編集完了画面を表示
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteInit() {
		return "client/user/update_complete";
	}
}