package jp.co.sss.shop.service;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.form.ItemForm;

/**
 * データベースのJSON属性を表示用Formに翻訳するコンバーター
 */
@Component
public class ItemViewConverter {

	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Entityから情報を読み取り、画面表示用のFormへ変換する
	 */
	public ItemForm convertToForm(Item entity) throws Exception {
		ItemForm form = new ItemForm();

		// 基本情報のセット
		form.setId(entity.getId());
		form.setName(entity.getName());
		form.setPrice(entity.getPrice());
		form.setStock(entity.getStock());
		form.setDescription(entity.getDescription());
		form.setImage(entity.getImage());
		form.setCategoryId(entity.getCategory() != null ? entity.getCategory().getId() : null);

		// JSON文字列を解析してFormにセット
		// variationJsonがnullの場合は空のJSONオブジェクトとして扱う
		String json = (entity.getVariationJson() != null) ? entity.getVariationJson() : "{}";
		JsonNode root = objectMapper.readTree(json);

		// カテゴリに応じてJSONから値を取り出し、Formへセット
		switch (entity.getCategory() != null ? entity.getCategory().getId() : 0) {
		case 1: // ボールペン
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setNibDiameter(root.path("nib_diameter").asText(""));
			break;
		case 2: // シャーペン
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setLeadDiameter(root.path("lead_diameter").asText(""));
			break;
		case 3: // 万年筆
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setNibDiameter(root.path("nib_diameter").asText(""));
			break;
		case 4: // ガラスペン
			form.setColorPattern(root.path("color_pattern").asText(""));
			break;
		case 5: // インク
			form.setInkVolume(root.path("ink_volume").asText(""));
			break;
		case 6: // その他
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setLeadDiameter(root.path("lead_diameter").asText(""));
			break;
		}

		return form;
	}
}