package jp.co.sss.shop.controller.client.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.PromotionsRepository;

/**
 * カルーセル広告クラス
 * @author 山口（チームF）
 */
@Controller
public class ClientAdShowController {

	/** 商品情報を管理するリポジトリ */
	@Autowired
	ItemRepository itemRepository;

	/** 広告情報を管理するリポジトリ */
	@Autowired
	PromotionsRepository promotionsRepository;

	

	/**
	 * 
	 * @author 川合（チームF）
	 * @param id 広告のID
	 * @param model 画面に渡すデータを保持するモデル
	 * @return 広告ページを表示するためのコントローラー
	 * @throws Exception ファイル未発見
	 */
//	@RequestMapping(path = "/client/ad/page/{id}", method = RequestMethod.GET)
//	public String page(@PathVariable Integer id, Model model) throws Exception {
//
//		//広告情報を取得
//		Promotions ad = promotionsRepository.findById(id).orElseThrow();
//
//		//JSONを扱うために分解
//		ObjectMapper mapper = new ObjectMapper();
//
//		//DBから取得
//		String json = ad.getContentJson();
//
//		//JSON全体をJsonNodeに変換
//		JsonNode root = mapper.readTree(json);
//
//		//JSONの画像の部分
//		JsonNode image = root.path("レイアウトによって変化する項目")
//				.path("画像")
//				.get(0);
//
//		//JSONのURLの部分
//		JsonNode link = root.path("レイアウトによって変化する項目")
//				.path("リンク")
//				.get(0);
//
//		// JSONから遷移先URLを取得 金城
//		JsonNode linkURL = root.path("レイアウトによって変化する項目").path("リンク").get(0);
//		String targetUrlFromJson = linkURL.path("URL").asText();
//
//		model.addAttribute("ad", ad);
//		model.addAttribute("targetUrl", targetUrlFromJson);
//
//		//画面に投げる
//		model.addAttribute("ad", ad);
//
//		//ペー名用
//		model.addAttribute("pageName", root.path("ページ名").asText());
//
//		//タイトル用
//		model.addAttribute("adTitle", root.path("広告題名").asText());
//
//		//広告画像用
//		model.addAttribute("imageName", root.path("imageName").asText());
//
//		//本文用
//		model.addAttribute("description", root.path("本文").asText());
//
//		//画像用
//		model.addAttribute("headingImage", image.path("src").asText());
//
//		//URL用
//		model.addAttribute("targetUrl", linkURL.path("URL").asText());
//
//		//リンク名用
//		model.addAttribute("linkTexts", linkURL.path("表示文字").asText());
//
//		return "client/ad/page";
//	}

}
