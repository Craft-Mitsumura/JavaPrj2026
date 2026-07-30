package jp.co.sss.shop.controller.client.user;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.PrizeRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.MailService;

/**
 * 会員情報詳細のコントローラクラス
 *
 * @author Hirai Toshiki / 手塚
 */
@Controller
public class ClientUserShowController {

	private final MailService mailService;
	/**
	 * ユーザリポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * プライズリポジトリ
	 */
	@Autowired
	PrizeRepository prizeRepository;

	/**
	 * 注文一覧リポジトリ
	 */
	@Autowired
	OrderRepository orderRepository;

	ClientUserShowController(MailService mailService) {
		this.mailService = mailService;
	}
	/**
	 * 会員情報詳細画面
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/detail 会員情報詳細画面を表示
	 */
	@RequestMapping(path = "/client/user/detail")
	public String detail(Model model, HttpSession session) {

		// セッションからログイン中のユーザー情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// DBから取得
		User userEntity = userRepository.findById(loginUser.getId()).orElse(null);

		List<Order> orderList = orderRepository.findByUserIdOrderByInsertDateDesc(userEntity.getId());
		model.addAttribute("orderList", orderList);

		Prize nextPrize = prizeRepository
				.findFirstByRequiredPointGreaterThanOrderByRequiredPointAsc(userEntity.getPoint());

		// エンティティからのデータをBeanに詰め替える
		UserBean userBean = new UserBean();

		userBean.setEmail(userEntity.getEmail());
		userBean.setPassword(userEntity.getPassword());
		userBean.setName(userEntity.getName());
		userBean.setPostalCode(userEntity.getPostalCode());
		userBean.setAddress(userEntity.getAddress());
		userBean.setPhoneNumber(userEntity.getPhoneNumber());
		// ID
		userBean.setId(userEntity.getId());
		// 権限
		userBean.setAuthority(userEntity.getAuthority());
		//ポイント
		userBean.setPoint(userEntity.getPoint());

		if (nextPrize != null) {
			model.addAttribute("nextPrizeName", nextPrize.getName());
			model.addAttribute("nextPrizePoint", nextPrize.getRequiredPoint());
			model.addAttribute("nextPrizeImage", nextPrize.getImage());

			int remainPoint = nextPrize.getRequiredPoint() - userEntity.getPoint();
			model.addAttribute("remainPoint", remainPoint);
		}

		// 変更前の情報をセッションに保存
		session.setAttribute("pastUser", userBean);

		// リクエストスコープに保存
		model.addAttribute("userBean", userBean);

		return "client/user/detail";
	}

	/**
	 * 会員情報登録入力画面
	 * @author 手塚
	 * @param model リクエストスコープ
	 * @return client/user/regist_input 会員情報登録入力画面を表示
	 */
	@RequestMapping("/client/user/regist/input")
	public String registInput(Model model
			) {

		// 入力フォーム用の会員情報を作成
		UserBean userBean = new UserBean();

		model.addAttribute("userForm", userBean);

		// 会員登録入力画面へ遷移
		return "client/user/regist_input";
	}

	/**
	 * 会員情報登録確認画面（論理削除対応版）
	 * @author 手塚
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

	    // バリデーションエラーがあるかどうか
	    boolean hasError = result.hasErrors();

	    // メールアドレス重複チェック（論理削除済みも含む）
	    User duplicateUser = userRepository.findByEmail(userBean.getEmail());

	    if (duplicateUser != null) {

	        // 現役会員ならエラー
	        if (duplicateUser.getDeleteFlag() == 0) {
	            result.rejectValue("email", "msg.regist.email.duplicate");
	            hasError = true;
	        }

	        // 退会済み会員ならIDを引き継ぐ
	        if (duplicateUser.getDeleteFlag() == 1) {
	            userBean.setId(duplicateUser.getId());
	        }

	    } else {
	        // 完全新規
	        userBean.setId(null);
	    }

	    // エラーが1つでもあれば入力画面へ戻る
	    if (hasError) {
	        return "client/user/regist_input";
	    }

	    // 登録する会員情報をセッションに保存
	    session.setAttribute("registUser", userBean);

	    // 確認画面へ渡す
	    model.addAttribute("userForm", userBean);

	    // 確認画面へ
	    return "client/user/regist_check";
	}
	/**
	 * 会員情報登録完了処理（新規追加 ＆ 既存データの復活UPDATE対応版）
	 * @author 手塚
	 * @param session セッションスコープ
	 * @return client/user/regist_complete 会員情報登録完了画面を表示
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String registComplete(HttpSession session) {

		// セッションから登録する会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("registUser");

		User user;

		// セッションのBeanにIDが入っているかで、新規か再登録かを自動分岐
		if (userBean.getId() != null) {
			// 既存の退会済みデータをデータベースから取得して上書き対象にする（再登録）
			user = userRepository.findById(userBean.getId()).orElse(new User());
		} else {
			// 完全な新規エンティティを作成し、新規登録時のみ初期ポイントを付与
			user = new User();
			user.setId(null);
			user.setPoint(0);
		}

		// 上書き保存
		user.setEmail(userBean.getEmail());
		user.setPassword(userBean.getPassword());
		user.setName(userBean.getName());
		user.setPostalCode(userBean.getPostalCode());
		user.setAddress(userBean.getAddress());
		user.setPhoneNumber(userBean.getPhoneNumber());

		// 一般会員として登録
		user.setAuthority(2);

		// 重要：削除フラグを 0 (未削除・現役) に戻する
		user.setDeleteFlag(0);

		// DBへ保存
		userRepository.save(user);

		// 登録情報をセッションから削除
		session.removeAttribute("registUser");

		// 会員登録完了画面へ遷移
		return "client/user/regist_complete";
	}
	
	/**
	 * 会員情報編集画面
	 * @author 手塚
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/update_input 会員情報編集画面を表示
	 */
	@RequestMapping("/client/user/update/input")
	public String updateInput(@ModelAttribute("userForm") UserBean userBean, Model model, HttpSession session) {

		// ログイン中の会員情報を取得
		UserBean pastUser = (UserBean) session.getAttribute("pastUser");

		// 各項目をセット
		userBean.setEmail(pastUser.getEmail());
		userBean.setPassword(pastUser.getPassword()); 
		userBean.setName(pastUser.getName());
		userBean.setPostalCode(pastUser.getPostalCode());
		userBean.setAddress(pastUser.getAddress());
		userBean.setPhoneNumber(pastUser.getPhoneNumber());
		userBean.setId(pastUser.getId());
		userBean.setAuthority(pastUser.getAuthority());
		userBean.setPoint(pastUser.getPoint());

		// 編集画面へ遷移
		return "client/user/update_input";
	}

	/**
	 * 会員情報編集確認画面（重複チェックの論理削除バグ修正、空欄回避対応版）
	 * @author 手塚
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
			@RequestParam(value = "newEmail", required = false) String newEmail,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			Model model,
			HttpSession session) {

		// ログイン中の情報（変更前の情報）取得
		UserBean pastUser = (UserBean) session.getAttribute("pastUser");

		boolean hasError = false;

		// 旧メールアドレスと旧パスワードの未入力チェック
		if (oldEmail == null || oldEmail.trim().isEmpty()) {
			model.addAttribute("oldEmailErrorMessage", "メールアドレスを入力してください。");
			hasError = true;
		}
		if (oldPassword == null || oldPassword.trim().isEmpty()) {
			model.addAttribute("oldPasswordErrorMessage", "パスワードを入力してください。");
			hasError = true;
		}

		// 新しいメールアドレスが入力されていたら上書き、空欄なら元のメールアドレスをそのまま引き継ぐ
		if (newEmail != null && !newEmail.trim().isEmpty()) {

		    // ★メールアドレス形式チェック
		    if (!newEmail.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
		        model.addAttribute("newEmailErrorMessage", "メールアドレスの形式が正しくありません。");
		        hasError = true;
		    }

		    // ★形式が正しい場合だけ重複チェック
		    if (!hasError && !newEmail.equals(pastUser.getEmail())) {
		    	User duplicateUser = userRepository.findByEmail(newEmail);
		        if (duplicateUser != null) {
		            model.addAttribute("authErrorMessage", "入力された新しいメールアドレスは既に登録されています。");
		            hasError = true;
		        }
		    }

			userBean.setEmail(newEmail);
		} else {
			userBean.setEmail(pastUser.getEmail()); 
		}

		// 新しいパスワードが入力されていたら上書き、空欄なら元のパスワードをそのまま引き継ぐ
		if (newPassword != null && !newPassword.trim().isEmpty()) {

		    // 8～16文字チェック
		    if (newPassword.length() < 8 || newPassword.length() > 16) {
		        model.addAttribute("newPasswordErrorMessage",
		                "パスワードは8～16文字で入力してください。");
		        hasError = true;
		    }

		    userBean.setPassword(newPassword);

		} else {
		    userBean.setPassword(pastUser.getPassword());
		}

		// 旧メールアドレスと旧パスワードの確認
		if (!hasError) {

		    if (!oldEmail.equals(pastUser.getEmail())) {
		        model.addAttribute("oldEmailErrorMessage",
		                "登録されているメールアドレスと一致しません。");
		        hasError = true;
		    }

		    if (!oldPassword.equals(pastUser.getPassword())) {
		        model.addAttribute("oldPasswordErrorMessage",
		                "登録されているパスワードと一致しません。");
		        hasError = true;
		    }
		}
		// 名前、郵便番号、住所、電話番号の個別必須チェック
		if (userBean.getName() == null || userBean.getName().trim().isEmpty()) {
			result.rejectValue("name", "msg.regist.input");
			hasError = true;
		}
		if (userBean.getPostalCode() == null || userBean.getPostalCode().trim().isEmpty()) {
			result.rejectValue("postalCode", "msg.regist.input");
			hasError = true;
		}
		if (userBean.getAddress() == null || userBean.getAddress().trim().isEmpty()) {
			result.rejectValue("address", "msg.regist.input");
			hasError = true;
		}
		if (userBean.getPhoneNumber() == null || userBean.getPhoneNumber().trim().isEmpty()) {
			result.rejectValue("phoneNumber", "msg.regist.input");
			hasError = true;
		}

		// 必須項目チェックに引っかかった場合は入力画面へ戻す
		if (hasError) {
			System.out.println("hasError = true");
			model.addAttribute("oldEmail", oldEmail);
			model.addAttribute("newEmail", newEmail);
			model.addAttribute("userForm", userBean);
			return "client/user/update_input";
		}

		// 編集後の会員情報を一時保存して確認画面へ
		session.setAttribute("updateUser", userBean);
		model.addAttribute("userForm", userBean);
		
		return "client/user/update_check";
	}

	/**
	 * 会員情報編集完了処理（新規追加・リダイレクト対応）
	 * @author 手塚
	 * @param session セッションスコープ
	 * @return 会員情報編集完了画面表示へリダイレクト
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete(HttpSession session) {

		// セッションから確認画面で一時保存した編集後の会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("updateUser");

		if (userBean != null) {
			// データベースから現在の会員情報をIDをキーに取得
			User user = userRepository.findById(userBean.getId()).orElse(null);

			if (user != null) {
				// 各項目を画面からの新しい入力値で上書き
				user.setEmail(userBean.getEmail());
				user.setPassword(userBean.getPassword());
				user.setName(userBean.getName());
				user.setPostalCode(userBean.getPostalCode());
				user.setAddress(userBean.getAddress());
				user.setPhoneNumber(userBean.getPhoneNumber());

				// 上書き保存
				userRepository.save(user);

				// 新しい情報に更新する
				session.setAttribute("user", userBean);
			}
		}

		// 編集用の一時セッションをきれいに削除
		session.removeAttribute("updateUser");

		// 二重送信を防ぐため、完了画面の表示URLへリダイレクト
		return "redirect:/client/user/update/complete/init";
	}

	/**
	 * 会員情報編集完了画面表示（新規追加）
	 * @author 手塚
	 * @return client/user/update_complete 編集完了画面を表示
	 */
	@RequestMapping(path = "/client/user/update/complete/init", method = RequestMethod.GET)
	public String updateCompleteInit() {
		return "client/user/update_complete";
	}

	/**
	 * 会員情報削除確認画面
	 * @author 手塚
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
	 * @author 手塚
	 * @param session セッションスコープ
	 * @return アカウント削除完了画面へリダイレクト
	 */
	@RequestMapping("/client/user/delete/complete")
	public String deleteComplete(HttpSession session) {

		UserBean loginUser = (UserBean) session.getAttribute("user");

		User user = userRepository.findById(loginUser.getId()).orElse(null);

		if (user != null) {
			// 削除フラグを1（退会済み）にして上書き保存する
			user.setDeleteFlag(1);
			userRepository.save(user);
		}

		return "redirect:/client/user/delete/complete/init";
	}

	/**
	 * 会員情報削除完了画面表示
	 * @param session セッションスコープ
	 * @return client/user/delete_complete 削除完了画面
	 */
	@RequestMapping(path = "/client/user/delete/complete/init", method = RequestMethod.GET)
	public String deleteCompleteInit(HttpSession session) {
		
		// 画面表示時に安全にセッションを無効化（ログアウト）
		session.invalidate();
 
		return "client/user/delete_complete";
	}
	


	    @GetMapping("/client/review/form")
	    public String showReviewForm() {
	    	System.out.println("triggred");
	        return "client/review/reviewForm";
	    }

	    @PostMapping("/client/review/input")
	    public String review(
	            @RequestParam String name,
	            @RequestParam String email,
	            @RequestParam String subject,
	            @RequestParam String message
	           ) {

	        mailService.sendMail(name, email, subject , message);

	        return "redirect:/";
	    }
	}
	

