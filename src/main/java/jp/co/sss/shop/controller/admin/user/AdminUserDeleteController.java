package jp.co.sss.shop.controller.admin.user;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 削除機能(運用管理者、システム管理者)のコントローラクラス
 *
 * @author SystemShared
 * 
 *  TIPS: 一般会員向けの会員削除機能に類似した処理です。
 *  
 */
@Controller
public class AdminUserDeleteController {

	/**
	 * 会員情報 リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 会員情報削除確認処理
	 *
	 * @param id 削除対象ID
	 * @return "redirect:/admin/user/delete/check" 削除確認画面 表示
	 */
	@RequestMapping(path = "/admin/user/delete/check/{id}", method = RequestMethod.POST)
	public String deleteCheck(@PathVariable Integer id) {

		// 削除対象の会員情報を取得
		User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

		if (user == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// 取得情報から表示フォーム情報を生成
		UserForm userForm = new UserForm();
		BeanUtils.copyProperties(user, userForm);

		//情報フォームをセッションに保持
		session.setAttribute("userForm", userForm);

		// 削除確認画面　表示
		return "redirect:/admin/user/delete/check";
	}

	/**
	 * 確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/user/delete_check" 確認画面 表示
	 */
	@RequestMapping(path = "/admin/user/delete/check", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから入力フォーム取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 入力フォーム情報を画面表示設定
		model.addAttribute("userForm", userForm);

		// 削除確認画面　表示
		return "admin/user/delete_check";
	}

	/**
	 * 会員情報削除完了処理
	 *
	 * @return "redirect:/admin/user/delete/complete" 会員情報 削除完了画面へ
	 */
	@RequestMapping(path = "/admin/user/delete/complete", method = RequestMethod.POST)
	public String deleteComplete() {

		// セッションから削除対象フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 削除対象の会員情報を取得
		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);

		if (user == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// 削除フラグを立てる
		user.setDeleteFlag(Constant.DELETED);

		// 会員情報を保存
		userRepository.save(user);

		// セッションの削除対象情報を削除
		session.removeAttribute("userForm");

		// 削除完了画面　表示処理
		return "redirect:/admin/user/delete/complete";
	}

	/**
	 * 会員情報削除完了処理
	 *
	 * @return "admin/user/delete_complete" 会員情報 削除完了画面へ
	 */
	@RequestMapping(path = "/admin/user/delete/complete", method = RequestMethod.GET)
	public String deleteCompleteFinish() {

		return "admin/user/delete_complete";
	}

}
