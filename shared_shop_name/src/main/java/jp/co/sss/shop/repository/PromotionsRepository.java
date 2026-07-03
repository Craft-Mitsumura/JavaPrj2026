package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Promotions;

/**
 * @author	金城（チームF）
 * Promotionsテーブル用リポジトリ
 * 
 */
@Repository
public interface PromotionsRepository extends JpaRepository<Promotions, Integer> {

	/**
	 * トップページ用：有効な広告をすべて取得
	 * @param deleteFlag 削除フラグ
	 * @return 広告エンティティ
	 */
	List<Promotions> findByDeleteFlagOrderByIsActiveDescIdAsc(Integer deleteFlag);

	//広告ページ用：IDで特定
	//findByIdで代用

	/**
	 * システム運用者用：広告一覧
	 * @param deleteFlag 削除フラグ
	 * @return 広告エンティティ
	 */
	List<Promotions> findByDeleteFlagOrderByInsertDateDesc(Integer deleteFlag);
}
