package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * @author Sharma Sagar
 * 買い物かごのコントロール
 */
@Controller
public class ClientBasketController {
	
	/**
	 * アイテムリポジトリ
	 */	
	@Autowired
	ItemRepository itemRepository;

	/**
	 * @author Sarma Sagar	
	 * @return 商品一覧に飛びます
	 * @param id を受け渡しするため.
	 * @param session userのかご情報を知るため
	 *  
	 */
	/*
	 * @RequestMapping(path = "client/basket/add", method = { RequestMethod.GET,
	 * RequestMethod.POST }) public String add(HttpSession session,
	 * 
	 * @RequestParam Integer id,
	 * 
	 * @RequestParam(required = false) String from) { // itemにはidの値が同じである場合情報代入する
	 * Item item = itemRepository.findById(id).orElse(null); //
	 * list型にsessionからbasketBeanの値を取得している List<BasketBean> basketBeans =
	 * (List<BasketBean>) session.getAttribute("basketBeans");
	 * 
	 * if (basketBeans == null) { // 買い物かごが空の場合 basketBeans = new ArrayList<>();
	 * BasketBean basketBeanNew = new
	 * BasketBean(item.getId(),item.getName(),item.getStock());
	 * basketBeans.add(basketBeanNew); } else { for (int i = 0; i <
	 * basketBeans.size(); i++) { // 買い物かごに同じ商品がすでにある場合、数量だけ増やす if
	 * (basketBeans.get(i).getId() == item.getId()) {
	 * basketBeans.get(i).setOrderNum(basketBeans.get(i).getOrderNum() + 1); break;
	 * } else if((i + 1) == basketBeans.size()) { // なかった場合、商品はカゴに入れる BasketBean
	 * basketBeanNew = new BasketBean(item.getId(),item.getName(),item.getStock());
	 * basketBeans.add(basketBeanNew); break; } }
	 * 
	 * } // sessionスコープにbasketBeansの値を代入する session.setAttribute("basketBeans",
	 * basketBeans);
	 * 
	 * if ("favorite".equals(from)) { return "redirect:/client/basket/list"; }
	 * 
	 * return "redirect:/client/item/list/1";
	 * 
	 * }
	 */
@RequestMapping(path="/client/basket/add", method ={RequestMethod.GET, RequestMethod.POST})
public String addToCart (HttpSession session ,
		Model model ,
		@RequestParam Integer id
		
		) {

	System.out.println(id);
	Item item = itemRepository.findById(id).orElse(null);
	model.addAttribute(item);
	return"/client/favorite/list";
	/* return "redirect:/client/item/list"; */
}

	/**
	 * 
	 * こちらは商品購入一覧のコントローラでございます。	
	 * @return 商品一覧に飛びます
	 * @param model basketbeansの情報を受け渡しするため.
	 * @param session userのかご情報を知るため
	 *  @throws InterruptedException エラーを行ったときcatchのため
	 */

	@RequestMapping(path = "/client/basket/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String basketList(HttpSession session, Model model) throws InterruptedException {
		// list型にsessionからbasketBeanの値を取得している。
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		// requestスコープにbasketBeansの値を代入
		model.addAttribute("basketBeans", basketBeans);

		return "client/basket/list";
	}

	/**
	 * 
	 * こちらは商品購入単品削除のコントローラでございます。
	 * @return 商品購入一覧に飛びます
	 * @param id を受け渡しするため.
	 * @param session userのかご情報を知るため
	 *  
	 */
	@RequestMapping(path = "/client/basket/delete")
	public String delete(@RequestParam Integer id, HttpSession session) {
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		if (basketBeans != null) {
			// もしitemのidとvalueが同じである場合
			basketBeans.removeIf(item -> item.getId().equals(id));
			// basketBeansには何もなかった場合	
			if (basketBeans.isEmpty()) {
				session.removeAttribute("basketBeans");
			} else {
				session.setAttribute("basketBeans", basketBeans);
			}
		}
		// 商品一覧のところ移動する	
		return "redirect:/client/basket/list";
	}

	/**
	 *
	 * こちらは商品購入全削除のコントローラでございます。
	 * @return 商品購入一覧に飛びます
	 * @param session userのかご情報を知るため。
	 *  
	 */
	@RequestMapping(path = "/client/basket/allDelete", method = { RequestMethod.POST, RequestMethod.GET })
	public String deleteAll(HttpSession session) {
	// sessionスコープにの値削除する
		session.removeAttribute("basketBeans");
	// 商品一覧のところ移動する
		return "redirect:/client/basket/list";
	}

}
