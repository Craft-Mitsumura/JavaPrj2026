package jp.co.sss.shop.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Rankings;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
	        @Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);
	
	/**
	 * 全件ランキング検索
	 * @param salesMonth 売上月
	 * @param pageable 最大10件
	 * @return ランキングエンティティ
	 * @author 小松原愛
	 */
	@Query("SELECT i FROM Item i JOIN FETCH i.category c JOIN Rankings r ON r.item.id = i.id " +
	       "WHERE r.salesMonth = :salesMonth ORDER BY r.total DESC")
	List<Rankings> findItemsOrderByallRanking(@Param("salesMonth") LocalDate salesMonth, Pageable pageable);
	
	/**
	 * カテゴリ別ランキング検索
	 * @param salesMonth 売上月
	 * @param categoryId カテゴリ
	 * @param pageable 最大10件
	 * @return ランキングエンティティ
	 * @author 小松原愛
	 */

	@Query("SELECT i FROM Item i JOIN FETCH i.category c JOIN Rankings r ON r.item.id = i.id " +
		    "WHERE r.salesMonth = :salesMonth AND c.id = :categoryId ORDER BY r.total DESC")
		List<Rankings> findItemsOrderBycateRanking(@Param("salesMonth") LocalDate salesMonth, @Param("categoryId") Integer categoryId, Pageable pageable);

		

	
	/**
	 * 商品情報を登録日付順に取得（一般会員用）
	 * @author 手塚
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	List<Item> findAllByDeleteFlagOrderByInsertDateDesc(int deleteFlag);

	/**
	 * カテゴリを指定して商品情報を登録日付順に取得
	 * @author 手塚
	 * @param deleteFlag 削除フラグ
	 * @param categoryId カテゴリID
	 * @return 商品エンティティのリスト
	 */
	List<Item> findAllByDeleteFlagAndCategoryIdOrderByInsertDateDesc(
	        int deleteFlag,
	        Integer categoryId);
	
	/**
	 * 商品名を部分一致検索（新着順）
	 * @author 手塚
	 * @param name 商品名
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	List<Item> findAllByNameContainingAndDeleteFlagOrderByInsertDateDesc(
	        String name,
	        int deleteFlag);
	
	/**
	 * 売れ筋順（注文個数の多い順）で未削除の商品一覧を取得します。（チームF:臨時追加）
	 *
	 * @return 売れ筋順の商品リスト
	 */
	@Query("SELECT i FROM Item i LEFT JOIN i.orderItemList oi WHERE i.deleteFlag = 0 GROUP BY i ORDER BY COALESCE(SUM(oi.quantity), 0) DESC")
	List<Item> findAllOrderBySales();
	List<Item> findByCategoryId(int id);
}



