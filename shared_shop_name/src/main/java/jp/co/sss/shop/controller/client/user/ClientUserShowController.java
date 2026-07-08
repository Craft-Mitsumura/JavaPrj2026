package jp.co.sss.shop.controller.client.user;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@RequestMapping(path="/client/user/detail")
	public String detail(Model model, HttpSession session){
		
		// セッションからログイン中のユーザー情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");
		
		// DBから取得
		User userEntity = userRepository.findById(loginUser.getId()).orElse(null);
		
		List<Order> orderList = orderRepository.findByUserIdOrderByInsertDateDesc(userEntity.getId());
		model.addAttribute("orderList", orderList);
		
		Prize nextPrize =
			    prizeRepository.findFirstByRequiredPointGreaterThanOrderByRequiredPointAsc(userEntity.getPoint());

		
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

	    model.addAttribute("userBean", userBean);

	    return "client/user/delete/check";
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
	        user.setDeleteFlag(1);
	        userRepository.save(user);
	    }

	    session.invalidate();

	    return "redirect:/";
	}
}
