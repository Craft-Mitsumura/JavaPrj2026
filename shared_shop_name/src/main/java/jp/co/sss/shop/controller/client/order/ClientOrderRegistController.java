package jp.co.sss.shop.controller.client.order;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.Rankings;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.RankingRepository;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 注文登録処理コントローラークラス。
 * 注文手続きの開始、お届け先入力、注文確定などの処理を制御します。
 * 
 * @author Murai Toa
 */
@Controller
public class ClientOrderRegistController {

	/**
	 * ユーザ情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * 商品情報リポジトリ
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * 注文情報リポジトリ
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * 注文商品情報リポジトリ
	 */
	@Autowired
	OrderItemRepository orderItemRepository;
	
	/**
	 * ランキングリポジトリ
	 */
	@Autowired
	RankingRepository  rankingRepository;

	/**
	 * 【処理1】ご注文のお手続きボタン押下時処理。
	 * 
	 * セッションからログイン会員の情報を取得し、データベースより最新の会員情報を抽出します。
	 * 抽出した住所や氏名などの初期値を設定した注文フォームオブジェクト（OrderForm）を生成し、
	 * セッションスコープに保存した上で、届け先入力画面の表示処理へリダイレクトします。
	 * 
	 * 
	 * @param session セッション情報を管理するHttpSessionオブジェクト
	 * @return 届け先入力画面表示処理へのリダイレクトパス（"redirect:/client/order/address/input"）
	 */
	// 処理1 (ご注文のお手続きボタン 押下時処理) 
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String redirectOrderForm(HttpSession session) {

		// 注文入力情報フォームを作成
		OrderForm orderForm = new OrderForm();

		// セッションスコープからログイン会員情報を取得
		UserBean loginuser = (UserBean) session.getAttribute("user");

		// 取得したログイン会員情報のユーザIDを条件にDBからユーザ情報を取得
		User user = userRepository.getReferenceById(loginuser.getId());

		// 取得したユーザ情報を注文入力フォーム情報に設定
		orderForm.setId(user.getId());
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());

		// 注文入力フォーム情報の支払方法に初期値としてクレジットカードを設定
		orderForm.setPayMethod(1);

		// 注文入力フォーム情報をセッションスコープに保存
		session.setAttribute("orderForm", orderForm);

		// 届け先入力画面表示処理へリダイレクト
		return "redirect:/client/order/address/input";
	}

	/**
	 * 【処理2】届け先入力画面の表示処理を行います。
	 * セッションから注文フォームとエラー情報を取得し、リクエストスコープに設定します。
	 * 
	 * @param session セッション情報
	 * @param model 画面へ渡すオブジェクト
	 * @return 届け先入力画面のパス
	 */

	// 処理2 (届け先入力画面表示処理)
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String orderAddressInput(HttpSession session, Model model) {

		// セッションスコープから注文入力フォーム情報を取得 
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		// 注文入力フォーム情報をリクエストスコープに設定 
		model.addAttribute("orderForm", orderForm);

		// セッションスコープに入力エラー情報がある場合 
		String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + "orderForm";
		if (session.getAttribute(bindingResultKey) != null) {
			// - 取得したエラー情報をリクエストスコープに設定 
			model.addAttribute(bindingResultKey, session.getAttribute(bindingResultKey));
			// - セッションスコープから、エラー情報を削除 
			session.removeAttribute(bindingResultKey);
		} else {
			// エラー情報がセッションにない場合、Thymeleafの#fieldsエラーチェックでの画面クラッシュを防ぐために空の枠組みを設定
			model.asMap().computeIfAbsent(bindingResultKey,
					k -> new org.springframework.validation.BeanPropertyBindingResult(orderForm, "orderForm"));
		}

		// 登録画面表示  - フォワード: "client/order/address_input"
		return "client/order/address_input";
	}

	/**
	 * 【処理3】届け先入力画面の「次へ」ボタン押下時処理を行います。
	 * 入力値のエラーチェックを行い、エラー時は入力画面へ、正常時は支払方法選択画面へリダイレクトします。
	 * 
	 * @param form 画面からの入力フォームデータ
	 * @param result バリデーション結果
	 * @param session セッション情報
	 * @return 遷移先のリダイレクトパス
	 */
	// 処理3 (届け先入力画面 次へボタン 押下時処理)
	@RequestMapping(path = "client/order/payment/input", method = RequestMethod.POST)
	public String checkAddress(@Valid @ModelAttribute("orderForm") OrderForm form, BindingResult result,
			HttpSession session) {

		// セッションスコープから注文入力フォーム情報を取得 
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		// BindingResult オブジェクトに入力エラー情報がある場合 
		if (result.hasErrors()) {
			// - 入力エラー情報をセッションスコープに設定 
			String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + "orderForm";
			session.setAttribute(bindingResultKey, result);

			// - 届け先入力画面表示処理にリダイレクト 
			// リダイレクト : “/client/order/address/input” 
			return "redirect:/client/order/address/input";
		}

		// 画面からの最新の入力値をセッションの注文情報に設定して上書き保存
		if (orderForm != null) {
			orderForm.setPostalCode(form.getPostalCode());
			orderForm.setAddress(form.getAddress());
			orderForm.setName(form.getName());
			orderForm.setPhoneNumber(form.getPhoneNumber());
			session.setAttribute("orderForm", orderForm);
		}

		// 入力エラーがない場合 
		// 支払方法選択画面表示処理にリダイレクト 
		// リダイレクト : “/client/order/payment/input”
		return "redirect:/client/order/payment/input";
	}

	/**
	* 【処理4】支払方法選択画面の表示処理を行います。
	* セッションから注文フォームを取得し、支払い方法の初期値とともにリクエストスコープに設定します。
	* 
	* @param session セッション情報
	* @param model 画面へ渡すオブジェクト
	* @return 支払方法選択画面のパス
	*/
	// 処理４(支払方法選択画面表示処理）
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String choicePayment(HttpSession session, Model model) {

		// セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		// 注文フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);

		// 追記 支払方法初期値選択
		model.addAttribute("payMethod", orderForm.getPayMethod());

		// 支払方法選択画面表示
		return "client/order/payment_input";
	}

	/**
	 * 【処理5】支払方法選択画面の「次へ」ボタン押下時処理を行います。
	 * 画面から選択された支払方法をセッションの注文フォームに反映し、注文確認画面表示処理へリダイレクトします。
	 * 
	 * @param form 画面からの入力フォームデータ
	 * @param session セッション情報
	 * @return 遷移先のリダイレクトパス
	 */
	//　処理５(支払方法選択画面 次へボタン 押下時処理)
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String checkPayment(
			@RequestParam("payMethod") Integer payMethod,
			HttpSession session) {

		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		System.out.println("payMethod = " + payMethod);

		orderForm.setPayMethod(payMethod);

		session.setAttribute("orderForm", orderForm);

		return "redirect:/client/order/check";
	}

	/**
	* 【処理6】注文確認画面の表示処理を行います。
	* セッションから取得した買い物かごの在庫チェックを行い、必要に応じて注文数の調整や売り切れ商品の削除を
	* 行った上で、合計金額を含む注文情報をリクエストスコープに設定し、確認画面を表示します。
	* 
	* @param session セッション情報
	* @param model 画面へ渡すオブジェクト
	* @return 注文確認画面のパス
	*/
	//処理6(注文確認画面表示処理)
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String checkOrder(HttpSession session, Model model) {

		// セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		// セッションスコープから買い物かご情報を取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		List<BasketBean> updatedBasketList = new ArrayList<>();
		List<OrderItemBean> orderItemBeans = new ArrayList<>();

		List<String> itemNameListLessThan = new ArrayList<>();
		List<String> itemNameListZero = new ArrayList<>();

		if (basketBeans != null) {
			for (BasketBean basket : basketBeans) {

				// 注文商品の最新情報をDB から取得し、商品の在庫チェックを行う
				Item item = itemRepository.getReferenceById(basket.getId());
				int stock = item.getStock();
				int orderNum = basket.getOrderNum();

				// 在庫不足、在庫切れ商品がある場合
				if (stock == 0) {

					// - 在庫切れの商品は、買い物かごから削除
					itemNameListZero.add(basket.getName());
					continue;

				} else if (orderNum > stock) {

					basket.setOrderNum(stock);
					itemNameListLessThan.add(basket.getName());
				}

				// 在庫数にあわせて、買い物かご情報を更新（注文数、在庫数）
				updatedBasketList.add(basket);

				// 買い物かご情報から、商品ごとの金額小計を算出し、注文商品情報リストに保存
				OrderItemBean orderItem = new OrderItemBean();
				orderItem.setId(basket.getId());
				orderItem.setName(basket.getName());
				orderItem.setPrice(item.getPrice());
				orderItem.setImage(item.getImage());
				orderItem.setOrderNum(basket.getOrderNum());
				orderItem.setSubtotal(item.getPrice() * basket.getOrderNum());

				// 【追加】刻印情報をセット
				orderItem.setIsEngravingSelected(basket.isEngravingSelected());
				orderItem.setEngravingText(basket.getEngravingText());
				orderItem.setFontType(basket.getFontType());

				orderItemBeans.add(orderItem);
			}
		}

		// 在庫不足、在庫切れ商品がある場合
		if (!itemNameListLessThan.isEmpty()) {
			// 注文警告メッセージをリクエストスコープに保存
			model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		}
		if (!itemNameListZero.isEmpty()) {
			// 注文警告メッセージをリクエストスコープに保存
			model.addAttribute("itemNameListZero", itemNameListZero);
		}

		// 在庫状況を反映した買い物かご情報をセッションに保存
		if (updatedBasketList.isEmpty()) {
			session.removeAttribute("basketBeans");
		} else {
			session.setAttribute("basketBeans", updatedBasketList);
		}

		if (!orderItemBeans.isEmpty()) {
			// 注文商品情報リストから合計金額を算出する
			// 合計金額をリクエストスコープに設定
			model.addAttribute("total", orderItemBeans.stream().mapToInt(OrderItemBean::getSubtotal).sum());

			// 注文商品情報リストをリクエストスコープに設定
			model.addAttribute("orderItemBeans", orderItemBeans);

			// 注文入力フォーム情報をリクエストスコープに設定
			model.addAttribute("orderForm", orderForm);
		}

		// 注文確認画面表示
		return "client/order/check";
	}

	/**
	 * 注文確認画面からお届け先入力画面への戻るボタン押下処理を行います。
	 * 
	 * @return 届け先入力画面表示処理へのリダイレクトパス
	 */
	// 処理７（注文確認画面で、戻るボタン押下処理）
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String backView() {

		return "redirect:/client/order/address/input";
	}

	/**
	* 「ご注文の確定」ボタン押下時の注文確定処理を行います。
	* 購入商品の最終在庫チェック、注文情報および詳細情報のDB登録、消費期限が短いロットからの先入先出し（FIFO）による在庫減算、セッションのクリアを行い、注文完了画面へ遷移します。
	* 
	* @param session 注文フォームや買い物かご情報を管理するセッション
	* @return 在庫切れ時は注文確認画面、注文成功時は注文完了画面へのリダイレクトパス
	*/
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	@Transactional // 処理の高速化と、エラー時のデータ保護のために追加しています
	public String completeOrder(HttpSession session) {

		// セッションスコープから注文情報を取得 
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		// セッションスコープから買い物かご情報を取得 
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		// 注文商品の在庫チェックをする
		if (basketBeans == null || basketBeans.isEmpty() || basketBeans.stream().anyMatch(b -> {
			int stock = itemRepository.getReferenceById(b.getId()).getStock();
			// ・在庫切れまたは在庫不足の商品がある場合 
			return stock == 0 || b.getOrderNum() > stock;
		})) {
			// -注文確認画面表示処理へリダイレクト 
			return "redirect:/client/order/check";
		}

		// 注文エンティティに紐付けるために、DBから既存のユーザー情報だけを取得します
		User user = userRepository.findById(orderForm.getId()).orElse(new User());

		// 注文情報情報を元にDB 登録用エンティティオブジェクトを生成
		Order order = new Order();
		order.setUser(user); // 取得したユーザーを紐付け（

		order.setPostalCode(orderForm.getPostalCode());
		order.setAddress(orderForm.getAddress());
		order.setName(orderForm.getName());
		order.setPhoneNumber(orderForm.getPhoneNumber());
		order.setPayMethod(orderForm.getPayMethod());

		// 注文テーブルおよび注文商品テーブルのDB 登録実施
		// 注文情報を保存
		orderRepository.save(order);

		int totalPrice = 0;

		// 注文商品情報を保存
		for (BasketBean basket : basketBeans) {

		    // 商品情報取得
		    Item item = itemRepository.findById(basket.getId())
		            .orElseThrow(() -> new RuntimeException("商品が存在しません ID=" + basket.getId()));

		    System.out.println("===== 商品情報 =====");
		    System.out.println("Basket ID : " + basket.getId());
		    System.out.println("Item ID   : " + item.getId());

		    // 注文商品エンティティ作成
		    OrderItem orderItem = new OrderItem();

		    orderItem.setOrder(order);
		    orderItem.setItem(item);
		    orderItem.setQuantity(basket.getOrderNum());
		    orderItem.setPrice(item.getPrice());

		    // 保存
		    orderItemRepository.save(orderItem);

		    // 合計金額計算
		    totalPrice += item.getPrice() * basket.getOrderNum();

		    // 在庫更新
		    item.setStock(item.getStock() - basket.getOrderNum());
		    itemRepository.save(item);

		    // ===== ランキング更新 =====
		    Date salesMonth = Date.valueOf(LocalDate.now().withDayOfMonth(1));

		    Rankings ranking = rankingRepository.findBySalesMonthAndItem_Id(
		            salesMonth, item.getId());

		    if (ranking == null) {

		        System.out.println("ランキング新規作成");
		        System.out.println("保存するITEM_ID = " + item.getId());

		        ranking = new Rankings();
		        ranking.setSalesMonth(salesMonth);
		        ranking.setItem(item);
		        ranking.setTotal(basket.getOrderNum());

		        System.out.println("ranking.getItem() = "
		                + (ranking.getItem() == null ? "null" : ranking.getItem().getId()));

		        rankingRepository.save(ranking);

		    } else {

		        System.out.println("ランキング更新");
		        System.out.println("現在の合計 = " + ranking.getTotal());

		        ranking.setTotal(ranking.getTotal() + basket.getOrderNum());

		        rankingRepository.save(ranking);
		    }
		}

		// 購入ポイント加算（500円で1ポイント）
		int addPoint = totalPrice / 500;
		user.setPoint(user.getPoint() + addPoint);
		userRepository.save(user);

		session.removeAttribute("orderForm");
		session.removeAttribute("basketBeans");

		// 注文完了画面
		return "redirect:/client/order/complete";
	}

	/**
	* 注文完了画面の表示処理を行います。
	* 注文手続きが正常に終了したことを知らせる完了画面を呼び出します。
	* 
	* @return 注文完了画面のパス
	*/
	// 処理９(注文完了画面表示処理)
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderFinish() {

		return "client/order/complete";
	}
}