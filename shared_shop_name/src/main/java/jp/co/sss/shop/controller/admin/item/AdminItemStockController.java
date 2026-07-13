package jp.co.sss.shop.controller.admin.item;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;

@Controller
public class AdminItemStockController {
	


	/**
	 * 在庫追加機能と画面遷移を行うコントローラークラス
	 * 入力、確認、完了の各フェーズにおけるリクエスト処理、セッションスコープを利用した状態管理
	 * 入力値のチェックを行います。
	 * 入力値の二重登録防止でPRGパターンを適用しています。
	 * 
	 * @author 小松原愛
	 */

		/**
		 * 商品テーブル用のリポジトリ
		 */
		@Autowired
		ItemRepository itemRepository;

		/**
		 * 画面遷移時、データ保持のためのHTTPセッション
		 */
		@Autowired
		HttpSession session;

		/**
		 * 商品在庫ロットテーブル用リポジトリ
		 */
//		@Autowired
//		jp.co.sss.shop.repository.ItemStockBatchesRepository itemStockBatchesRepository;

		/**
		 * 在庫追加ボタン押下時、対象の商品情報を取得します。
		 * セッションスコープに保持し、入力画面にリダイレクトします。
		 * 
		 * @param id 対象となる商品ID
		 * @return　在庫追加入力画面("redirect:/admin/item/stock/input")、
		 * 対象がなければ商品一覧画面("redirect:/admin/item/list")
		 */
		@RequestMapping(path = "/admin/item/stock/input/{id}", method = RequestMethod.POST)
		public String addStockRegist(@PathVariable("id") Integer id) {

			// 処理1 (在庫追加ボタン 押下時処理) 、(確認画面-戻るボタン 押下時処理)
			// パスに指定のIDを条件に変更対象のデータをDBから取得
			Item itemOpt = itemRepository.findById(id).orElse(null);

			if (itemOpt == null) {
				return "redirect:/admin/item/list";
			}

			// 入力フォーム情報をセッションスコープに保存
			session.setAttribute("StockForm", itemOpt);

			// 入力画面表示処理へリダイレクト:"/admin/item /restock/input"
			return "redirect:/admin/item/restock/input";
		}

//	/**
//		 * 在庫追加入力画面を表示します。
//	 * セッションスコープから対象の情報を取得し、Modelに設定します。
//	 * 
//		 * @param model 画面へデータを渡すためのModelオブジェクト
//		 * @return 在庫追加入力("admin/item/restock_input")
//		 */
//		@RequestMapping(path = "/admin/item/restock/input", method = RequestMethod.GET)
//		public String addStockRegistView(Model model) {
//
//			// 処理2 (在庫追加入力画面表示処理)
//			Item itemOpt = (Item) session.getAttribute("ItemStockForm");
//
//			if (itemOpt == null) {
//				return "redirect:/admin/item/list";
//			}
//
//			// 商品情報を画面表示用に渡す
//			model.addAttribute("item", itemOpt);
//
//			// フォーム取得
//			ItemStockBatchesForm itemStockBatchesForm = (ItemStockBatchesForm) session.getAttribute("itemStockBatchesForm");
//			if(itemStockBatchesForm == null) {
//				itemStockBatchesForm = new ItemStockBatchesForm();
//			}
//			LocalDate localDate = LocalDate.now();
//			 itemStockBatchesForm.setProductionDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())); 
//			 itemStockBatchesForm.setItemId(itemOpt.getId());
//			
//
//			model.addAttribute("itemStockBatchesForm", itemStockBatchesForm);
//
//			BindingResult result = (BindingResult) session.getAttribute("result");
//
//			if (result != null) {
//
//				model.addAttribute(
//						"org.springframework.validation.BindingResult.itemStockBatchesForm",
//						result);
//
//				session.removeAttribute("result");
//			}
//
//			session.removeAttribute("itemStockBatchesForm");
//
//			return "admin/item/restock_input";
//		}
//
////		/**
//		 * 入力画面から確認ボタン押下時、入力値の検証を行い、遷移先を決定します。
//		 * エラーの場合、入力値をセッションスコープに保持して入力画面に遷移します。
//		 * 正常な場合、確認画面にリダイレクトします。
//		 * 
//		 * @param form 入力画面から送信されたフォームオブジェクト
//		 * @param result 入力チェックの結果を保持するオブジェクト
//		 * @return 入力エラー時は入力画面("redirect:/admin/item/restock/input")、正常時は確認画面("redirect:/admin/item/restock/check")
//		 */
//		// 処理3 (確認ボタン 押下時処理)
//		@RequestMapping(path = "/admin/item/restock/check", method = RequestMethod.POST)
//		public String addStockRegistCheck(
//				@jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute ItemStockBatchesForm form,
//				BindingResult result) {
//			// 画面から入力された入力フォームを、セッションスコープに入力フォーム情報として保存
//			session.setAttribute("itemStockBatchesForm", form);
////			Date expiredDate = form.getExpirationDate();
//
//			// BindingResultオブジェクトに入力エラー情報がある場合
//			if (result.hasErrors() || (form.getExpirationDate() == null) ) {
//
//				
//				// 入力エラー情報をセッションスコープに設定
//				session.setAttribute("result", result);
//
//				// 在庫追加入力画面表示処理にリダイレクト
//				return "redirect:/admin/item/restock/input";
//			}else if(result.hasErrors() && form.getExpirationDate().before(form.getProductionDate())){
//				result.rejectValue("expirationDate","error.expirationDateCheck","消費期限は製造日以降を入力してください");
//	            session.setAttribute("result", result);
//				// 入力エラー情報をセッションスコープに設定
//				session.setAttribute("result", result);
//
//				// 在庫追加入力画面表示処理にリダイレクト
//				return "redirect:/admin/item/restock/input";
//			}
//
//			// 入力エラーがない場合、在庫追加確認画面表示処理にリダイレクト
//			return "redirect:/admin/item/restock/check";
//		}
//
//		/**
//		 * 在庫追加確認画面を表示します。
//		 * セッションスコープから入力値を取得し、Modelに設定します。
//		 * 
//		 * @param model 画面へデータを渡すためのModelオブジェクト
//		 * @return 在庫追加確認画面("admin/item/restock_check")
//		 */
//		// 処理4 (在庫追加確認画面表示処理)
//		@RequestMapping(path = "/admin/item/restock/check", method = RequestMethod.GET)
//		public String addStockRegistCheckView(Model model) {
//
//			// セッションスコープからフォーム情報を取得
//			ItemStockBatchesForm form = (ItemStockBatchesForm) session.getAttribute("itemStockBatchesForm");
//
//			// 商品情報を取得して画面表示用にセット
//			Item itemOpt = (Item) session.getAttribute("ItemStockForm");
//
//			if (form == null || itemOpt == null) {
//				return "redirect:/admin/item/list";
//			}
//
//			// 画面（確認HTML）に渡すデータをModelに登録
//			model.addAttribute("item", itemOpt);
//			model.addAttribute("itemStockBatchesForm", form);
//
//			// 在庫追加確認画面表示へフォワード : “admin/item/restock_check”
//			return "admin/item/restock_check";
//		}
//
//		/**
//		 * 在庫追加確認画面から戻るボタン押下時、入力情報を保持しながら入力画面に遷移します。
//		 * 
//		 * @param form 確認画面から送信された現在の入力データ
//		 * @return　在庫追加入力画面("redirect:/admin/item/restock/input")
//		 */
//		// 戻るボタン押下時、確認画面に遷移する
//		@RequestMapping(path = "/admin/item/restock/back", method = RequestMethod.POST)
//		public String addStockRegistBack(
//				@org.springframework.web.bind.annotation.ModelAttribute ItemStockBatchesForm form) {
//
//			// 戻ってきた入力内容をセッションに詰めて、入力画面で再表示できるようにする
//			session.setAttribute("itemStockBatchesForm", form);
//
//			// 入力画面表示処理（GET）にリダイレクト
//			return "redirect:/admin/item/restock/input";
//		}
//
//		/**
//		 * 確認画面から登録ボタン押下時、入力値をエンティティに送ってDBに登録します。
//		 * 画面用のフォーム（java.util.Date）からDB用のエンティティ（java.sql.Date）への型変換を行います。
//		 * 保存後にセッションスコープを破棄して完了画面に遷移します。
//		 * 
//		 * @param form 確認画面からhidden属性経由で送信されたフォームオブジェクト
//		 * @return 追加完了画面("redirect:/admin/item/restock/complete")、セッションまたはフォームが空の場合は一覧画面("redirect:/admin/item/list")
//		 */
//		//処理5 (登録ボタン 押下時処理)
//		@RequestMapping(path = "/admin/item/restock/complete", method = RequestMethod.POST)
//		public String addStockRegistComplete(
//				@org.springframework.web.bind.annotation.ModelAttribute ItemStockBatchesForm form) {
//
//			// セッションスコープから入力フォーム情報を取得
//			Item itemOpt = (Item) session.getAttribute("ItemStockForm");
//
//			// セッションまたはフォームが空の場合は一覧へ戻す
//			if (form == null || itemOpt == null) {
//				return "redirect:/admin/item/list";
//			}
//
//			// 入力フォーム情報をもとにDB登録用エンティティオブジェクトを生成
//			ItemStockBatches stockBatch = new ItemStockBatches();
//
//			// フォームの値をエンティティに移し替える
//			stockBatch.setItem(itemOpt);
//			stockBatch.setQuantity(form.getQuantity());
//
//			// 解決策A(java.util.Date)の場合、JPA保存用に java.sql.Date に変換してセット
//			if (form.getProductionDate() != null) {
//				stockBatch.setProductionDate(new java.sql.Date(form.getProductionDate().getTime()));
//			}
//			if (form.getExpirationDate() != null) {
//				stockBatch.setExpirationDate(new java.sql.Date(form.getExpirationDate().getTime()));
//			}
//
//			// DB 登録実施
//			itemStockBatchesRepository.save(stockBatch);
//
//			// セッションスコープの入力フォーム情報削除
//			session.removeAttribute("itemStockBatchesForm");
//			session.removeAttribute("ItemStockForm");
//
//			// 追加完了画面表示処理にリダイレクト
//			return "redirect:/admin/item/restock/complete";
//		}
//
//		/**
//		 * 在庫追加完了画面を表示します。
//		 * 
//		 * @return 在庫追加完了画面("admin/item/restock_complete")
//		 */
//		// 処理6 (追加完了画面表示処理)
//		@RequestMapping(path = "/admin/item/restock/complete", method = RequestMethod.GET)
//		public String addStockRegistCompleteView() {
//
//			// 完了画面表示 フォワード:“admin/item/restock_complete”
//			return "admin/item/restock_complete";
//		}
//	}
//
//
}
