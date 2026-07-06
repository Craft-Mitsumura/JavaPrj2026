package jp.co.sss.shop.controller.client.order;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

@Controller
public class ClientOrderShowController {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	HttpSession session;

	@Autowired
	PriceCalc priceCalc;

	@Autowired
	BeanTools beanTools;

	@RequestMapping(path = "/client/order/detail/{id}")
	public String showOrder(@PathVariable int id, Model model) {

		// ログイン中の会員
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// 注文取得
		Order order = orderRepository.getReferenceById(id);

		// 自分以外の注文は表示させない
		if (!order.getUser().getId().equals(loginUser.getId())) {
			return "redirect:/client/user/detail";
		}

		// 注文情報
		OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

		// 注文商品情報
		List<OrderItemBean> orderItemBeanList =
				beanTools.generateOrderItemBeanList(order.getOrderItemsList());

		// 合計金額
		int total =
				priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeanList);

		model.addAttribute("order", orderBean);
		model.addAttribute("orderItemBeans", orderItemBeanList);
		model.addAttribute("total", total);

		return "client/order/detail";
	}
}