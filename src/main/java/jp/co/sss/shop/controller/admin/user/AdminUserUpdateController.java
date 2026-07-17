package jp.co.sss.shop.controller.admin.user;

import java.sql.Date;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 変更機能(運用管理者、システム管理者)のコントローラクラス
 *
 * @author SystemShared
 * 
 * TIPS: 一般会員向けの会員変更機能に類似した処理です。
 */
@Controller
public class AdminUserUpdateController {

	/**
	 * 会員情報　リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 入力画面　初期表示処理(POST)
	 * 
	 * @param id 変更対象ID
	 * @return "redirect:/admin/user/update/input" 入力録画面　表示処理
	 */
	@RequestMapping(path = "/admin/user/update/input/{id}", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {

		//セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {

			// セッション情報がない場合、詳細画面からの遷移と判断し初期値を準備
			// 変更対象の情報取得
			User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

			if (user == null) {
				// 対象が無い場合、エラー
				return "redirect:/syserror" ;
			}

			// 初期表示用フォーム情報の生成
			userForm = new UserForm();
			//変更対象の情報をuserFormにコピー
			BeanUtils.copyProperties(user, userForm);

			//変更入力フォームをセッションに保持
			session.setAttribute("userForm", userForm);

		}

		//変更入力画面　表示処理
		return "redirect:/admin/user/update/input";

	}

	/**
	 * 入力画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/user/update_input" 変更入力画面 表示
	 */
	@RequestMapping(path = "/admin/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから入力フォーム取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 入力フォーム情報を画面表示設定
		model.addAttribute("userForm", userForm);

		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報を画面表示設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("result");
		}

		//変更入力画面　表示
		return "admin/user/update_input";

	}

	/**
	 * 変更確認処理
	 *
	 * @param form 入力フォーム
	 * @param result 入力チェック結果
	 * @return 
	 *   入力値エラーあり："redirect:/admin/user/update/input" 変更入力画面へ 
	 *   入力値エラーなし："redirect:/admin/user/update/check" 変更確認画面へ
	 */
	@RequestMapping(path = "/admin/user/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		//直前のセッション情報を取得
		UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (lastUserForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}
		if(form.getAuthority()==null) {
			//権限情報がない場合、セッション情報から値をセット
			form.setAuthority(lastUserForm.getAuthority());
		}
		
		// 入力フォーム情報をセッションに保持
		session.setAttribute("userForm", form);

		// 入力値にエラーがあった場合、入力画面に戻る
		if (result.hasErrors()) {

			session.setAttribute("result", result);

			//変更入力画面　表示処理
			return "redirect:/admin/user/update/input";

		}

		//変更確認画面　表示処理
		return "redirect:/admin/user/update/check";
	}

	/**
	 * 確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/user/update_check" 確認画面表示
	 */
	@RequestMapping(path = "/admin/user/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {
		//セッションから入力フォーム情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		//入力フォーム情報をスコープへ設定
		model.addAttribute("userForm", userForm);

		// 変更確認画面　表示
		return "admin/user/update_check";

	}


	/**
	 * 変更登録、完了画面表示処理
	 *
	 * @return "redirect:/admin/user/update/complete" 変更完了画面　表示へ
	 */
	@RequestMapping(path = "/admin/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {

		//セッション保持情報から入力値再取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 変更対象情報を取得
		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);
		if (user == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror" ;
		}

		Integer deleteFlag = user.getDeleteFlag();
		Date insertDate = user.getInsertDate();

		// 入力フォーム情報を変更用エンティティに設定
		BeanUtils.copyProperties(userForm, user);

		// 入力値以外の項目を設定
		user.setDeleteFlag(deleteFlag);
		user.setInsertDate(insertDate);

		// 情報を保存
		userRepository.save(user);

		// ログインユーザ情報変更の場合、セッション保存ユーザ情報を更新
		UserBean loginUser = (UserBean)session.getAttribute("user") ; 
		if (loginUser.getId() == userForm.getId()) {
			loginUser.setName(userForm.getName()) ;
		}
		session.setAttribute("user", loginUser) ;
		
		//セッション情報の削除
		session.removeAttribute("userForm");

		// 変更完了画面　表示処理
		//二重送信防止のためリダイレクトを行う
		return "redirect:/admin/user/update/complete";
	}

	/**
	 * 変更完了画面　表示
	 * 
	 * @return "admin/user/update_complete"
	 */
	@RequestMapping(path = "/admin/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
		
		return "admin/user/update_complete";
	}

}
