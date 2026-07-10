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
		if (entity.getCategory() != null) {
			form.setCategoryId(entity.getCategory().getId());
			form.setCategoryName(entity.getCategory().getName());
		}

		// JSON文字列を解析してFormにセット
		// variationJsonがnullの場合は空のJSONオブジェクトとして扱う
		String json = (entity.getVariationJson() != null) ? entity.getVariationJson() : "{}";
		JsonNode root = objectMapper.readTree(json);

		// カテゴリに応じてJSONから値を取り出し、Formへセット
		int categoryId = (entity.getCategory() != null) ? entity.getCategory().getId() : 0;
		switch (categoryId) {
		case 1: // 万年筆
			form.setVarNumber(root.path("var_Number").asInt(0));
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setNibDiameter(root.path("nib_diameter").asText());
			break;
		case 2: // シャーペン
			form.setVarNumber(root.path("var_Number").asInt(0));
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setLeadDiameter(root.path("lead_diameter").asDouble());
			break;
		case 3: // ボールペン
			form.setVarNumber(root.path("var_Number").asInt(0));
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setNibDiameter(root.path("nib_diameter").asText());
			break;
		case 4: // ガラスペン
			form.setVarNumber(root.path("var_Number").asInt(0));
			form.setColorPattern(root.path("color_pattern").asText(""));
			break;
		case 5: // インク
			form.setVarNumber(root.path("var_Number").asInt(0));
			form.setInkVolume(root.path("ink_volume").asText(""));
			break;
		case 6: // その他
			form.setVarNumber(root.path("var_Number").asInt(0));
			form.setColorPattern(root.path("color_pattern").asText(""));
			form.setLeadDiameter(root.path("lead_diameter").asDouble());
			break;
		}

		return form;
	}
}