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
 * @author Hirai Toshiki
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
		userBean.setPassword(userEntity.getPassword());   // ←追加
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
	@RequestMapping("/client/user/regist/input/init")
	public String registInput(Model model) {

		// 入力フォーム用の会員情報を作成
		UserBean userBean = new UserBean();

		// リクエストスコープに保存　
		model.addAttribute("userForm", userBean);

		// 会員登録入力画面へ遷移
		return "client/user/regist_input";
	}

	/**
	 * 会員情報登録確認画面
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

		// 必須項目が未入力のとき
		if (result.hasErrors()) {
			return "client/user/regist_input";
		}
		
		// メールアドレス重複チェック
		User duplicateUser = userRepository.findByEmail(userBean.getEmail());

		if (duplicateUser != null) {
		    result.rejectValue("email", "msg.regist.email.duplicate");
		    return "client/user/regist_input";
		}

		// 登録する会員情報をセッションに保存
		session.setAttribute("registUser", userBean);

		// 確認画面へ渡す
		model.addAttribute("userForm", userBean);

		// 会員登録確認画面へ遷移
		return "client/user/regist_check";
	}

	/**
	 * 会員情報登録完了処理
	 * @author 手塚
	 * @param session セッションスコープ
	 * @return client/user/regist_complete 会員情報登録完了画面を表示
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String registComplete(HttpSession session) {

		// セッションから登録する会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("registUser");

		// Entityへデータを詰め替え
		User user = new User();

		user.setEmail(userBean.getEmail());
		user.setPassword(userBean.getPassword());
		user.setName(userBean.getName());
		user.setPostalCode(userBean.getPostalCode());
		user.setAddress(userBean.getAddress());
		user.setPhoneNumber(userBean.getPhoneNumber());

		// 一般会員として登録
		user.setAuthority(2);

		// 初期ポイントを設定
		user.setPoint(0);

		// DBへ保存
		userRepository.save(user);

		// 登録情報をセッションから削除
		session.removeAttribute("registUser");

		// 登録完了画面へ遷移
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
	 * 会員情報編集確認画面
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
			@RequestParam("oldEmail") String oldEmail,
			@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newEmail") String newEmail,
			@RequestParam("newPassword") String newPassword,
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
			// 他の人が使っていないか重複チェック
			if (!newEmail.equals(pastUser.getEmail())) {
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
			userBean.setPassword(newPassword);
		} else {
			userBean.setPassword(pastUser.getPassword()); // 変更なし（現在の値を設定）
		}

		// 旧メールアドレスと旧パスワードの本人確認
		if (!hasError) {
			if (!oldEmail.equals(pastUser.getEmail()) || !oldPassword.equals(pastUser.getPassword())) {
				model.addAttribute("authErrorMessage", "旧メールアドレスまたは旧パスワードが正しくありません。");
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

		// 必須項目チェック
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
	 * 会員情報編集完了処理
	 * @author 手塚
	 * @param session セッションスコープ
	 * @return client/user/update_complete 会員情報編集完了画面を表示
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete(HttpSession session, Model model) {

		// セッションから編集後の会員情報および現在のログインユーザー情報を取得
		UserBean updateUserBean = (UserBean) session.getAttribute("updateUser");
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// データベースから現在のユーザーエンティティを取得
		User user = userRepository.findById(loginUser.getId()).orElse(null);

		if (user != null) {
			// データベースのエンティティに入力された値を上書きする
			user.setEmail(updateUserBean.getEmail());
			user.setName(updateUserBean.getName());
			user.setPostalCode(updateUserBean.getPostalCode());
			user.setAddress(updateUserBean.getAddress());
			user.setPhoneNumber(updateUserBean.getPhoneNumber());

			// データベースへ保存
			userRepository.save(user);

			// セッションスコープ内のログインユーザー情報(Bean)も新しい内容に同期する
			loginUser.setEmail(user.getEmail());
			loginUser.setName(user.getName());
			loginUser.setPostalCode(user.getPostalCode());
			loginUser.setAddress(user.getAddress());
			loginUser.setPhoneNumber(user.getPhoneNumber());
			session.setAttribute("user", loginUser);
		}

		// 使用済みのセッション情報を削除
		session.removeAttribute("updateUser");

		// 編集完了画面
		return "client/user/update_complete";
	}

	/**
	 * 会員情報削除確認画面	 *
	 * @author 手塚
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/delete/check 会員情報削除確認画面を表示
	 */
	@RequestMapping("/client/user/delete/check")
	public String deleteCheck(Model model, HttpSession session) {

		UserBean userBean = (UserBean) session.getAttribute("pastUser");

		model.addAttribute("userForm", userBean);

		return "client/user/delete_check";
	}

	/**
	 * 会員情報削除処理
	 *
	 * @author 手塚
	 * @param session セッションスコープ
	 * @return トップ画面へリダイレクト
	 */
	@RequestMapping("/client/user/delete/complete")
	public String deleteComplete(HttpSession session) {

		UserBean loginUser = (UserBean) session.getAttribute("user");

		User user = userRepository.findById(loginUser.getId()).orElse(null);

		if (user != null) {
			// データベースからデータを完全に削除
			userRepository.delete(user);
		}

		session.invalidate();

		return "redirect:/";
	}
	


	    @GetMapping("/client/review/reviewForm")
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
	

