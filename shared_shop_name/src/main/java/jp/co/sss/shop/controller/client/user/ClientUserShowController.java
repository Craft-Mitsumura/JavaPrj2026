package jp.co.sss.shop.controller.client.user;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Prize;
import jp.co.sss.shop.entity.User;
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
		

}
