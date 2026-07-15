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
	@Query("SELECT r.item FROM Rankings r WHERE r.salesMonth = :salesMonth ORDER BY r.total DESC")
	List<Item> findItemsOrderByallRanking(@Param("salesMonth") LocalDate salesMonth, Pageable pageable);

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
	List<Item> findItemsOrderBycateRanking(@Param("salesMonth") LocalDate salesMonth,
			@Param("categoryId") Integer categoryId, Pageable pageable);

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
	 * 商品名またはカテゴリ名で部分一致検索（新着順）
	 *
	 * @author 手塚
	 * @param keyword 検索キーワード
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i JOIN i.category c WHERE i.deleteFlag = "
			+ ":deleteFlag AND ( i.name LIKE %:keyword%  OR c.name LIKE %:keyword% ) ORDER BY i.insertDate DESC ")
	List<Item> findByNameOrCategoryContaining(
			@Param("keyword") String keyword,
			@Param("deleteFlag") int deleteFlag);

	/**
	 * 売れ筋順（注文個数の多い順）で未削除の商品一覧を取得します。（チームF:臨時追加）
	 *
	 * @return 売れ筋順の商品リスト
	 */
	@Query("SELECT i FROM Item i LEFT JOIN i.orderItemList oi WHERE i.deleteFlag = 0 GROUP BY i ORDER BY COALESCE(SUM(oi.quantity), 0) DESC")
	List<Item> findAllOrderBySales();

	List<Item> findByCategoryId(Integer id);

	List<Item> findByNameContaining(String items);

	/**
	 * カテゴリ指定の商品一覧（ページング対応）
	 * @author 手塚
	 * @param id カテゴリID
	 * @param pageable ページ情報
	 * @return 商品ページ
	 */
	Page<Item> findByCategoryId(Integer id, Pageable pageable);
	
	/**
	 * 【トップページ用】未削除の新着商品を最大4件取得（IDの降順）
	 * @param deleteFlag 削除フラグ
	 * @return 新着商品4件のリスト
	 */
	@Query(value = "SELECT i FROM Item i WHERE i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC, i.id DESC")
	List<Item> findTop4ByDeleteFlagOrderByIdDesc(@Param("deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 【商品一覧画面用】未削除の商品を新着順（IDの降順）にページングして取得
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	Page<Item> findByDeleteFlagOrderByIdDesc(int deleteFlag, Pageable pageable);
	
	/**
	 * @author 金城 詳細表示用
	 * @param name
	 * @return
	 */
	List<Item> findByName(String name);
	List<Item> findByNameContainingIgnoreCaseAndDeleteFlag(String item, Integer deleteFlag);

	List<Item> findByCategoryIdAndDeleteFlag(Integer categoryId, Integer deleteFlag);
}
