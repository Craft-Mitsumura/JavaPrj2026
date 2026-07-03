//package jp.co.sss.shop.controller.client.item;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.Query;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import jp.co.sss.shop.entity.Rankings;
//import jp.co.sss.shop.repository.CategoryRepository;
//import jp.co.sss.shop.repository.ItemRepository;
//import jp.co.sss.shop.util.Constant;
//
///**
// * ランキングコントローラー
// * @author 小松原愛
// */ 
//@Controller
//public class ClientItemRankingShowController {
//
//	/**
//	 * アイテムレポジトリ
//	 */
//	@Autowired
//	ItemRepository itemrepo;
//	
//	@Autowired
//	CategoryRepository caterepo;
//
//	/**
//	 * @param model リクエストスコープ
//	 * @return /client/item/ranking/list ランキング表示
//	 */
//	@GetMapping("/client/item/ranking/{genderType}")
//	public String showItemList(@RequestParam(name = "categoryId", required = false) Integer categoryId, Model model) {
//
//		LocalDate today = LocalDate.now();
//		LocalDate firstDateOfMonth = today.withDayOfMonth(1);
//		Query query;
//		List<Rankings> rankingItems = new ArrayList<>();
//		// 画面のカテゴリー一覧に表示するため、すべてのカテゴリーを取得してModelにセット
//		model.addAttribute("categories", caterepo.findByIdAndDeleteFlag(Constant.NOT_DELETED, categoryId));
//		//		全件ランキング表示
//		if (categoryId != null) {
////			 カテゴリー別のNamedQueryを呼び出し、検索パラメータをセット
//			query = entityManager.createNamedQuery("findBySalesMonthAndCategoryNamedQuery");
//			query.setParameter("salesMonth", firstDateOfMonth);
//			query.setParameter("categoryId", categoryId);
//
//			// 画面の見出しを「〇〇年〇月度 [カテゴリー名]」にする
//			caterepo.findById(categoryId).ifPresent(c -> {
//				model.addAttribute("currentMonthText",
//						today.getYear() + "年" + today.getMonthValue() + "月度 [" + c.getName() + "]");
//			});
//
//			//  データベースからの最大取得件数を10件（10位まで）に制限
//			query.setMaxResults(10);
//
//			// 1. クエリから [ID, 合計値] のリストを取得
//			@SuppressWarnings("unchecked")
//			List<Object[]> queryResults = query.getResultList();
//
//			// 2. 画面（HTML）が求める商品情報のリストを作成
//
//			for (Object[] row : queryResults) {
//				// SELECTの1番目：i.items.id (商品ID)
//				//  Number型を経由して、安全にIntegerへ変換（キャストエラー対策）
//				Number idNum = (Number) row[0];
//				if (idNum == null)
//					continue;
//				Integer itemId = idNum.intValue();
//
//				// 主キー検索(findById)をやめ、商品IDをキーにListで取得する（最初に見つかった1件をベースにする）
//				List<Rankings> dbRankings = itemrepo.findByRanking();
//
//				if (dbRankings != null && !dbRankings.isEmpty()) {
//					// ベースとなる商品情報を1件目から取得
//					Rankings rankingData = dbRankings.get(0);
//
//				}
//			}
//		}
//		//		カテゴリー別ランキング表示
//		else{
//			// 通常の全体用NamedQueryを呼び出す
//			query = entityManager.createNamedQuery("findBySalesMonthNamedQuery");
//			query.setParameter("salesMonth", firstDateOfMonth);
//
//			// 画面の見出しを「〇〇年〇月度 」にする
////			model.addAttribute("currentMonthText", today.getYear() + "年" + today.getMonthValue() + "月度");
//
//			//  データベースからの最大取得件数を10件（10位まで）に制限
//			query.setMaxResults(10);
//
//			// 1. クエリから [ID, 合計値] のリストを取得
//			@SuppressWarnings("unchecked")
//			List<Object[]> queryResults = query.getResultList();
//
//			for (Object[] row : queryResults) {
//				// SELECTの1番目：i.items.id (商品ID)
//				//  Number型を経由して、安全にIntegerへ変換（キャストエラー対策）
//				Number idNum = (Number) row[0];
//				if (idNum == null)
//					continue;
//				Integer itemId = idNum.intValue();
//
//				// 主キー検索(findById)をやめ、商品IDをキーにListで取得する（最初に見つかった1件をベースにする）
//				List<Rankings> dbRankings = repoRanking.findByItemsId(itemId);
//
//				if (dbRankings != null && !dbRankings.isEmpty()) {
//					// ベースとなる商品情報を1件目から取得
//					Rankings rankingData = dbRankings.get(0);
//
//					// SELECTの2番目：SUM(i.total) (売上数量の合計)
//					//  Number型を経由して、安全に数量の合計値をセット
//					Number totalSum = (Number) row[1];
//					if (totalSum != null) {
//						// 男女合算された正しい合計値を上書きセットする
//						rankingData.setTotal(totalSum.intValue());
//					}
//
//					rankingItems.add(rankingData);
//				}
//			}
//		}
//
//		}
//
//		// 3. 正しいデータが入ったリストを画面に渡す
//		model.addAttribute("rankingItems", rankingItems);
////		model.addAttribute("genderType", genderType);
//		model.addAttribute("categoryId_", categoryId);
//		return "client/item/ranking/list";
//	}}