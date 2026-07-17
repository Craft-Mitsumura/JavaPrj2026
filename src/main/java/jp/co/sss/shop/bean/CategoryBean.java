package jp.co.sss.shop.bean;

/**
 * カテゴリ情報クラス
 *
 * @author SystemShared
 */
public class CategoryBean {

	/**
	 * カテゴリID
	 */
	private Integer id;
	/**
	 * カテゴリ名
	 */
	private String name;
	/**
	 * カテゴリ説明
	 */
	private String description;

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
