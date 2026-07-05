package jp.co.sss.shop.controller.client.item;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.entity.Rankings;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.Constant;

/**
 * ランキングコントローラー
 * @author 小松原愛
 */

@Controller
public class ClientItemRankingShowController {

	/**
	 * アイテムレポジトリ
	 */
	@Autowired
	ItemRepository itemrepo;

	@Autowired
	CategoryRepository caterepo;

	/**
	 * @param model リクエストスコープ
	 * @return 
	 */

	@GetMapping("/client/item/ranking/{genderType}")
	public String showItemList(@RequestParam(name = "categoryId", required = false) Integer categoryId, Model model) {

		LocalDate today = LocalDate.now();
		LocalDate firstDateOfMonth = today.withDayOfMonth(1);
		List<Rankings> findByRanking = new ArrayList<>();
		// 画面のカテゴリー一覧に表示するため、すべてのカテゴリーを取得してModelにセット
		model.addAttribute("categories", caterepo.findByIdAndDeleteFlag(Constant.NOT_DELETED, categoryId));
		//		全件ランキング表示
		if (categoryId == null) {

			// 通常の全体用NamedQueryを呼び出す
			findByRanking = itemrepo.findItemsOrderByallRanking(firstDateOfMonth, PageRequest.of(0, 10));

			//				 画面の見出しを「〇〇年〇月度 」にする
			model.addAttribute("currentMonthText", today.getYear() + "年" + today.getMonthValue() + "月度 [総合ランキング]");
			//			
		}

		else {

			//		カテゴリー別ランキング表示
			findByRanking = itemrepo.findItemsOrderBycateRanking(firstDateOfMonth, categoryId, PageRequest.of(0, 10));

			// 画面の見出しを「〇〇年〇月度 [カテゴリー名]」にする
			caterepo.findById(categoryId).ifPresent(c -> {
				model.addAttribute("currentMonthText",
						today.getYear() + "年" + today.getMonthValue() + "月度 [" + c.getName() + "]");
			});

			//  正しいデータが入ったリストを画面に渡す
			model.addAttribute("rankkings", findByRanking);

		}
		return "client/item/Ranking";

	}

}
