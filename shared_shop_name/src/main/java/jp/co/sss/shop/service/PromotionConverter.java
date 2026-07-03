package jp.co.sss.shop.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jp.co.sss.shop.entity.Promotions;
import jp.co.sss.shop.form.PromotionsForm;

/**
 * @author	金城（チームF）
 * 広告情報のコンバーター
 * */

@Component
public class PromotionConverter {

	/** JSON処理用のオブジェクトマッパー */
	private ObjectMapper objectMapper = new ObjectMapper(); // 初期化を追加

	/**
	 * Formの内容をEntityに詰め替える
	 * @param form 広告登録・変更フォーム
	 * @return 変換後の広告エンティティ
	 * @throws Exception JSON変換処理等で例外が発生した場合
	 */
	public Promotions convertToEntity(PromotionsForm form) throws Exception {
		Promotions entity = new Promotions();
		entity.setId(form.getId());
		entity.setLayoutType(form.getLayoutType());
		entity.setTargetUrl(form.getTargetUrl());
		entity.setIsActive(form.getIsActive());

		entity.setImageName(form.getTempImageName());

		// JSONの組み立て
		String contentJson = buildContentJson(form);
		entity.setContentJson(contentJson);

		return entity;
	}

	/**
	 * 実際にJSONを組み立てるメソッド
	 * @param form 広告登録・変更フォーム
	 * @return 組み立てられたJSON形式の文字列
	 * @throws Exception JSONへの変換処理（シリアライズ）に失敗した場合
	 */
	private String buildContentJson(PromotionsForm form) throws Exception {
		ObjectNode root = objectMapper.createObjectNode();
		root.put("ページ名", form.getPageName());
		root.put("広告題名", form.getAdTitle());
		root.put("タイトル画像", form.getTempHeadingImage());
		root.put("本文", form.getBody());

		ObjectNode dynamicContent = root.putObject("レイアウトによって変化する項目");

		// 画像の組み立て
		ArrayNode imageArray = dynamicContent.putArray("画像");
		List<String> tempFiles = form.getTempImageSrcs();
		if (tempFiles != null) {
			for (int i = 0; i < tempFiles.size(); i++) {
				ObjectNode img = imageArray.addObject();

				img.put("src", tempFiles.get(i));

				img.put("説明", (form.getImageTexts() != null && form.getImageTexts().size() > i)
						? form.getImageTexts().get(i)
						: "");
				img.put("alt", (form.getImageAlts() != null && form.getImageAlts().size() > i)
						? form.getImageAlts().get(i)
						: "");
			}
		}

		// リンクの組み立て
		ArrayNode linkArray = dynamicContent.putArray("リンク");
		if (form.getLinkUrls() != null) {
			for (int i = 0; i < form.getLinkUrls().size(); i++) {
				ObjectNode link = linkArray.addObject();
				link.put("表示文字", form.getLinkTexts().get(i));
				link.put("URL", form.getLinkUrls().get(i));
			}
		}

		return objectMapper.writeValueAsString(root);
	}

	/**
	 * Entityの内容をFormに詰め替える
	 * @param entity 広告エンティティ
	 * @return 復元された広告フォームオブジェクト
	 * @throws Exception JSONの解析処理（デシリアライズ）に失敗した場合
	 */
	public PromotionsForm convertToForm(Promotions entity) throws Exception {
		PromotionsForm form = new PromotionsForm();
		form.setId(entity.getId());
		form.setLayoutType(entity.getLayoutType());
		form.setTargetUrl(entity.getTargetUrl());
		form.setIsActive(entity.getIsActive());

		// 画像ファイル名の復元
		form.setTempImageName(entity.getImageName());

		// JSONの解析とFormへの詰め戻し
		com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(entity.getContentJson());
		form.setPageName(root.get("ページ名").asText());
		form.setAdTitle(root.get("広告題名").asText());
		form.setTempHeadingImage(root.get("タイトル画像").asText());
		form.setBody(root.get("本文").asText());

		return form;
	}

	/**
	 * 更新・登録共通：Entityの内容をFormの内容で上書き更新
	 *
	 * @param form   広告変更フォーム
	 * @param entity 更新対象の広告エンティティ
	 * @return 更新された広告エンティティ
	 * @throws Exception JSON文字列への再変換処理等で例外が発生した場合
	 */
	public Promotions updateEntityFromForm(PromotionsForm form, Promotions entity) throws Exception {
		entity.setLayoutType(form.getLayoutType());
		entity.setTargetUrl(form.getTargetUrl());
		entity.setIsActive(form.getIsActive());
		entity.setImageName(form.getTempImageName());
		entity.setIsActive(form.getIsActive() != null ? form.getIsActive() : 0);

		// JSON文字列への変換ロジックを共通利用
		entity.setContentJson(buildContentJson(form));

		return entity;
	}
}