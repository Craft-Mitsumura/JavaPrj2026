package jp.co.sss.shop.controller.client.user;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員情報登録のコントローラクラス
 *
 * @author 手塚 / 金城
 */
@Controller
public class ClientUserRegistController {

	@Autowired
	UserRepository userRepository;

	/**
	 * 会員情報登録入力画面 初期化処理
	 * @param session セッションスコープ
	 * @return "redirect:/client/user/regist/input" 入力画面へのリダイレクト
	 */
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.POST)
	public String registInputInit(HttpSession session) {

		session.removeAttribute("registUser");
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 会員情報登録入力画面
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/regist_input 会員情報登録入力画面を表示
	 */
	@RequestMapping("/client/user/regist/input")
	public String registInput(Model model, HttpSession session) {

		UserBean userBean = (UserBean) session.getAttribute("registUser");
		if (userBean == null) {
			userBean = new UserBean();
		}

		model.addAttribute("userForm", userBean);
		return "client/user/regist_input";
	}

	/**
	 * 会員情報登録確認画面
	 * @param userBean 入力された会員情報
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/regist_check 会員情報登録確認画面を表示
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String registCheck(
			@Valid @ModelAttribute("userForm") UserBean userBean,
			BindingResult result,
			Model model,
			HttpSession session) {

		boolean hasError = result.hasErrors();

		int targetAuthority = 2;
		Optional<User> duplicateUserOpt = userRepository.findByEmailAndAuthority(userBean.getEmail(), targetAuthority);

		if (duplicateUserOpt.isPresent()) {
			// デリートフラグのON/OFFに関わらず、同権限・同メアドが存在する場合はエラーとする
			result.rejectValue("email", "msg.regist.email.duplicate");
			hasError = true;
		} else {
			// 新規登録なのでIDは必ずクリアしておく
			userBean.setId(null);
		}

		if (hasError) {
			return "client/user/regist_input";
		}

		session.setAttribute("registUser", userBean);
		model.addAttribute("userForm", userBean);

		return "client/user/regist_check";
	}

	/**
	 * 会員情報登録完了処理
	 * @param session セッションスコープ
	 * @return client/user/regist_complete 会員情報登録完了画面を表示
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String registComplete(HttpSession session) {

		UserBean userBean = (UserBean) session.getAttribute("registUser");
		User user;

		if (userBean.getId() != null) {
			user = userRepository.findById(userBean.getId()).orElse(new User());
		} else {
			user = new User();
			user.setId(null);
			user.setPoint(0);
		}

		user.setEmail(userBean.getEmail());
		user.setPassword(userBean.getPassword());
		user.setName(userBean.getName());
		user.setPostalCode(userBean.getPostalCode());
		user.setAddress(userBean.getAddress());
		user.setPhoneNumber(userBean.getPhoneNumber());
		user.setAuthority(2);
		user.setDeleteFlag(0);

		userRepository.save(user);
		session.removeAttribute("registUser");

		return "redirect:/client/user/regist/complete";
	}

	@GetMapping("/client/user/regist/complete")
	public String completePage() {
		return "client/user/regist_complete";
	}
}