package jp.co.sss.shop.controller.admin.user;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 表示機能(運用管理者、システム管理者)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminUserShowController {
	/**
	 * 会員情報　リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 一覧表示処理
	 *
	 * @param model  Viewとの値受渡し
	 * @param pageable ページング制御
	 * @return "admin/user/list" 一覧画面　表示
	 */
	@RequestMapping(path = "/admin/user/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUserList(Model model, Pageable pageable) {

		// 会員情報の登録数の取得と新規追加可否チェック
		//count()メソッドを使用してレコード数を取得
		Long usersCount = userRepository.count();
		Boolean registrable = true;
		
		if (usersCount == Constant.USERS_MAX_COUNT) {
			registrable = false;
		}

		// 会員情報リストを取得
		Integer authority = ((UserBean) session.getAttribute("user")).getAuthority();
		//表示画面でページングが必要なため、ページ情報付きの検索を行う
		Page<User> userList = userRepository.findUsersListOrderByInsertDate(Constant.NOT_DELETED, authority, pageable);

		// 会員情報をViewに渡す
		model.addAttribute("registrable", registrable);
		model.addAttribute("pages", userList);
		//ページ情報付きの検索結果からgetContent()メソッドを使用してレコード情報のみを取り出す
		model.addAttribute("users", userList.getContent());
		

		//会員登録・変更・削除用のセッションスコープを初期化
		session.removeAttribute("userForm");

		// 一覧表示
		return "admin/user/list";
	}

	/**
	 * 詳細表示処理
	 *
	 * @param id 表示対象会員ID
	 * @param model Viewとの値受渡し
	 * @return "admin/user/detail" 会員詳細表示画面へ
	 * 
	 * TIPS: 一般会員向けの会員詳細表示機能に類似した処理です。
	 */
	@RequestMapping(path = "/admin/user/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUser(@PathVariable int id, Model model) {
		// 表示対象の情報を取得
		User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (user == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// Userエンティティの各フィールドの値をUserBeanにコピー
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		// 会員情報をViewに渡す
		model.addAttribute("userBean", userBean);

		//会員登録・変更・削除用のセッションスコープを初期化
		session.removeAttribute("userForm");

		// 詳細画面　表示
		return "admin/user/detail";
	}
}
