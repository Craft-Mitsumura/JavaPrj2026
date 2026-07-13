package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jp.co.sss.shop.annotation.CategoryCheck;

/**
 * カテゴリ情報のフォーム
 *
 * @author SystemShared
 * 
 * TIPS 入力チェックアノテーションのmessage属性に"{messages.propertiesで指定した名前}"と記述することができます。
 * 
 */
@CategoryCheck
public class CategoryForm implements Serializable {

	/**
	 * カテゴリID
	 */
	private Integer id;

	/**
	 * カテゴリ名
	 */
	@NotBlank(message = "カテゴリ名は必須項目です。")
	@Size(min = 1, max = 15, message = "カテゴリ名は15文字以内で入力してください。")
	private String name;

	/**
	 * カテゴリ説明
	 */
	@NotBlank(message = "説明文は必須項目です。")
	@Size(min = 1, max = 30, message = "説明文は30文字以内で入力してください。")
	private String description = "";

	/**
	 * カテゴリIDの取得
	 * @return カテゴリID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * カテゴリIDのセット
	 * @param id カテゴリID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * カテゴリ名の取得
	 * @return カテゴリ名
	 */
	public String getName() {
		return name;
	}

	/**
	 * カテゴリ名のセット
	 * @param name カテゴリ名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * カテゴリ説明文の取得
	 * @return カテゴリ説明文
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * カテゴリ説明文のセット
	 * @param description カテゴリ説明文
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
