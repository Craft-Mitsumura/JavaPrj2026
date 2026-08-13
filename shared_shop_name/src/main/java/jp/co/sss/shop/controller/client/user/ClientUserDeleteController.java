package jp.co.sss.shop.controller.client.user;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員情報削除のコントローラクラス
 *
 * @author 手塚
 */
@Controller
public class ClientUserDeleteController {

	@Autowired
	UserRepository userRepository;

	/**
	 * 会員情報削除確認画面
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/delete_check 会員情報削除確認画面を表示
	 */
	@RequestMapping("/client/user/delete/check")
	public String deleteCheck(Model model, HttpSession session) {

		UserBean userBean = (UserBean) session.getAttribute("pastUser");
		model.addAttribute("userForm", userBean);

		return "client/user/delete_check";
	}

	/**
	 * 会員情報削除処理（退会処理・論理削除）
	 * @param session セッションスコープ
	 * @return アカウント削除完了画面へリダイレクト
	 */
	@RequestMapping("/client/user/delete/complete")
	public String deleteComplete(HttpSession session) {

		UserBean loginUser = (UserBean) session.getAttribute("user");
		User user = userRepository.findById(loginUser.getId()).orElse(null);

		if (user != null) {
			user.setDeleteFlag(1);
			userRepository.save(user);
		}

		return "redirect:/client/user/delete/complete";
	}

	/**
	 * 会員情報削除完了画面表示
	 * @param session セッションスコープ
	 * @return client/user/delete_complete 削除完了画面
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String deleteCompleteInit(HttpSession session) {

		session.invalidate();
		return "client/user/delete_complete";
	}
}