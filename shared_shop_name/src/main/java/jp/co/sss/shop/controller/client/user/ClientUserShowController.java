package jp.co.sss.shop.controller.client.user;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.PrizeRepository;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員情報詳細のコントローラクラス
 *
 * @author Hirai Toshiki
 */
@Controller
public class ClientUserShowController {

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
	 *
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
	 *
	 * @param userBean 入力された会員情報
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/regist_check 会員情報登録確認画面を表示
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String registCheck(
			@ModelAttribute("userForm") UserBean userBean,
			Model model,
			HttpSession session) {

		// 登録する会員情報をセッションに保存
		session.setAttribute("registUser", userBean);

		// 確認画面へ渡す
		model.addAttribute("userForm", userBean);

		// 会員登録確認画面へ遷移
		return "client/user/regist_check";
	}

	/**
	 * 会員情報登録完了処理
	 *
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
	 *
	 * @author 手塚
	 * @param model リクエストスコープ
	 * @param session セッションスコープ
	 * @return client/user/update/input 会員情報編集画面を表示
	 */
	@RequestMapping("/client/user/update/input")
	public String updateInput(Model model, HttpSession session) {

		// セッションから変更前の会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("pastUser");

		// リクエストスコープに保存
		model.addAttribute("userForm", userBean);

		// 編集画面へ遷移
		return "client/user/update/input";
	}

	/**
	 * 会員情報削除確認画面
	 *
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
}

