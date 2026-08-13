package jp.co.sss.shop.controller.admin.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員管理 登録機能(運用管理者、システム管理者)のコントローラクラス
 *
 * @author SystemShared
 * 
 * TIPS: 一般会員向けの会員登録機能に類似した処理です。
 * 
 */
@Controller
public class AdminUserRegistController {

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
	 * 入力画面　表示処理(POST) 一覧画面での新規ボタン押下後の処理
	 * 
	 * @return "redirect:/admin/user/regist/input" 入力画面　表示処理
	 */
	@RequestMapping(path = "/admin/user/regist/input", method = RequestMethod.POST)
	public String registInput() {

		//セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			userForm = new UserForm();
			userForm.setAuthority(((UserBean) session.getAttribute("user")).getAuthority());

			//空の入力フォーム情報をセッションに保持 登録ボタンからの遷移
			session.setAttribute("userForm", userForm);
		}

		//登録入力画面　表示処理
		return "redirect:/admin/user/regist/input";

	}

	/**
	 * 入力画面　表示処理(GET)
	 * 
	 * @param model Viewとの値受渡し
	 * @return "admin/user/regist_input" 入力画面　表示
	 */
	@RequestMapping(path = "/admin/user/regist/input", method = RequestMethod.GET)
	public String registInput(Model model) {

		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			// セッションにエラー情報を削除
			session.removeAttribute("result");
		}

		// カスタムエラー情報をセッションから取得してモデルに追加
		@SuppressWarnings("unchecked")
		Map<String, String> errorMessages = (Map<String, String>) session.getAttribute("errorMessages");
		if (errorMessages != null && !errorMessages.isEmpty()) {
			model.addAttribute("errorMessages", errorMessages);
			session.removeAttribute("errorMessages");
		}

		// 入力フォーム情報をスコープに設定
		model.addAttribute("userForm", userForm);

		// 入力画面　表示
		return "admin/user/regist_input";

	}

	/**
	 * 登録入力確認　処理
	 *
	 * @param email メールアドレス
	 * @param password パスワード
	 * @param name 会員名
	 * @param postalCode 郵便番号
	 * @param address 住所
	 * @param phoneNumber 電話番号
	 * @param model Modelオブジェクト
	 * @return 
	 * 	入力値エラーあり："redirect:/admin/user/regist/input" 入力画面　表示処理
	 * 	入力値エラーなし："redirect:/admin/user/regist/check" 登録確認画面　表示処理
	 */
	@RequestMapping(path = "/admin/user/regist/check", method = RequestMethod.POST)
	public String Registcheck(
			@RequestParam(value="email", required = false) String email,
			@RequestParam(value="password", required = false) String password,
			@RequestParam(value="name", required = false) String name,
			@RequestParam(value="postalCode", required = false) String postalCode,
			@RequestParam(value="address", required = false) String address,
			@RequestParam(value="phoneNumber", required = false) String phoneNumber,
			Model model
	
			){
		
		int authority = ((UserBean) session.getAttribute("user")).getAuthority();
	Optional<User> hasEmail =userRepository.findByEmailAndAuthority(email , authority);
	
		boolean hasError = false;
		Map<String, String> errorMessages = new HashMap<>();
		UserForm lastUserForm =(UserForm) session.getAttribute("userForm");
		
		if(lastUserForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}
		if(lastUserForm.getAuthority() == null) {
			//権限情報がない場合、セッション情報から値をセット
			lastUserForm.setAuthority(((UserBean) session.getAttribute("user")).getAuthority());
		}
		
		// ユーザー入力値をフォームに保存（バリデーション前に設定）
		lastUserForm.setEmail(email);
		lastUserForm.setPassword(password);
		lastUserForm.setName(name);
		lastUserForm.setPostalCode(postalCode);
		lastUserForm.setAddress(address);
		lastUserForm.setPhoneNumber(phoneNumber);
		
		// バリデーション処理開始
		if(email == null || email.isEmpty()) {
	 errorMessages.put("email", "メールアドレスは必須項目です。");
	 
      hasError = true;	 
		}else if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
			errorMessages.put("email", "メールアドレスは正しい形式で入力してください。");
			hasError = true;
		}else if(hasEmail.isPresent()) {
			errorMessages.put("email", "このメールアドレスは既に登録されています。");
			hasError = true;
			
		}
		
		if(password == null || password.isEmpty()) {
			errorMessages.put("password", "パスワードは必須項目です。");
			hasError = true;
		}else if(!password.matches("^[a-zA-Z0-9]+$")) {
			errorMessages.put("password", "パスワードは正しい形式で入力してください。");
			hasError = true;
		}else if(password.length() < 8 || password.length() > 15) {
			errorMessages.put("password", "パスワードは8文字以上15文字以内で入力してください。");
			hasError = true;
		}
		
		if(name == null || name.isEmpty()) {
			errorMessages.put("name", "会員名は必須項目です。");
			hasError = true;
		}else if (name.length() > 30)  {
			errorMessages.put("name", "氏名は30文字以内で入力してください。");
			hasError = true;
		}
		if(postalCode == null || postalCode.isEmpty()) {
			errorMessages.put("postalCode", "郵便番号は必須項目です。");
			hasError = true;
		}else if(!postalCode.matches("^[0-9]+$")) {
			errorMessages.put("postalCode", "郵便番号は半角数字で入力してください。");
			hasError = true;
		}else if(postalCode.length() != 7) {
			errorMessages.put("postalCode", "郵便番号は7桁で入力してください。");
			hasError = true;
		}
		
		if(address == null || address.isEmpty()) {
			errorMessages.put("address", "住所は必須項目です。");
			hasError = true;
		}else if(address.length() > 150) {
			errorMessages.put("address", "住所は150文字以内で入力してください。");
			hasError = true;
		}
		
		if(phoneNumber == null || phoneNumber.isEmpty()) {
			errorMessages.put("phoneNumber", "電話番号は必須項目です。");
			hasError = true;
		}else if(!phoneNumber.matches("^[0-9]+$")) {
			errorMessages.put("phoneNumber", "電話番号は半角数字で入力してください。");
			hasError = true;
		}else if(phoneNumber.length() < 10 || phoneNumber.length() > 11) {
			errorMessages.put("phoneNumber", "電話番号は10文字以上11文字以内で入力してください。");
			hasError = true;
		}
		
		if(hasError) {
			// エラー情報をセッションに保存（redirect後も保持するため）
			session.setAttribute("errorMessages", errorMessages);
			// 入力フォーム情報をセッションに保持
			session.setAttribute("userForm", lastUserForm);
			// 登録入力画面 表示処理
			return "redirect:/admin/user/regist/input";
		}
	
		// エラーがない場合、セッションに保存
		session.setAttribute("userForm", lastUserForm);
		
		return"redirect:/admin/user/regist/check";
	}

	/**
	 * 確認画面　表示処理 
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/user/regist_check" 確認画面　表示
	 */
	@RequestMapping(path = "/admin/user/regist/check", method = RequestMethod.GET)
	public String registCheck(Model model) {
		//セッションから入力フォーム情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		//入力フォーム情報をスコープへ設定
		model.addAttribute("userForm", userForm);

		//登録確認画面　表示処理
		return "admin/user/regist_check";

	}

	/**
	 * 情報登録処理
	 *
	 * @return "redirect:/admin/user/regist/complete" 登録完了画面　表示処理
	 */
	@RequestMapping(path = "/admin/user/regist/complete", method = RequestMethod.POST)
	public String registComplete() {

		//セッション保持情報から入力値再取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 会員情報を生成
		User user = new User();

		// 入力フォーム情報をエンティティに設定
		BeanUtils.copyProperties(userForm, user);
         user.setPoint(0); // 初期ポイントを0に設定
		// DB登録
		userRepository.save(user);

		//セッションから入力情報削除
		session.removeAttribute("userForm");

		//登録完了画面　表示処理
		//二重送信防止のためリダイレクトを行う
		return "redirect:/admin/user/regist/complete";
	}

	/**
	 * 登録完了画面　表示処理
	 *
	 * @return "admin/user/regist_complete" 登録完了画面　表示
	 */
	@RequestMapping(path = "/admin/user/regist/complete", method = RequestMethod.GET)
	public String registCompleteFinish() {

		return "admin/user/regist_complete";
	}

}
