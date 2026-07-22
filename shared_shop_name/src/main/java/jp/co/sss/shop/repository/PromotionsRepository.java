package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Promotions;

/**
 * @author 金城（チームF）
 * Promotionsテーブル用リポジトリ
 */
@Repository
public interface PromotionsRepository extends JpaRepository<Promotions, Integer> {

	/**
	 * トップページ用：有効フラグの降順、IDの昇順で広告をすべて取得
	 * @return 広告エンティティのリスト
	 */
	List<Promotions> findAllByOrderByIsActiveDescIdAsc();
}