package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;

/**
 * お気に入り検索
 * 
 * @author 田中（チームF）
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    /**
     * 会員と商品からお気に入り検索
     * 
     * @param  user		   対象会員
     * @param  item		　 対象商品
     * @param  deleteFlag　削除フラグ
     * @return お気に入り検索
     */
    Favorite findByUserAndItemAndDeleteFlag(
            User user,
            Item item,
            Integer deleteFlag);

    
    /**
     * 会員と商品からお気に入り検索
     * 
     * @param  user				対象会員
     * @param  item				対象商品
     * @return お気に入り検索
     */
    Favorite findByUserAndItem(
            User user,
            Item item);
    
    /**
     * 会員のお気に入り一覧
     * 
     * @param  user					対象会員
     * @param  deleteFlag			削除フラグ
     * @return 会員のお気に入り一覧
     */
    List<Favorite> findByUserAndDeleteFlagOrderByInsertDateDesc(
            User user,
            Integer deleteFlag);
    
    /**
     * 会員のお気に入り一覧
     * 
     * @param  userId				対象会員ID
     * @param  deleteFlag			削除フラグ
     * @return 会員のお気に入り一覧
     */
    List<Favorite> findByUser_IdAndDeleteFlagOrderByInsertDateDesc(
            Integer userId,
            Integer deleteFlag);
    
    /**
     * 会員ID・商品IDからお気に入り検索
     *
     * @param userId 会員ID
     * @param itemId 商品ID
     * @param deleteFlag 削除フラグ
     * @return お気に入り
     */
    Favorite findByUser_IdAndItem_IdAndDeleteFlag(
            Integer userId,
            Integer itemId,
            Integer deleteFlag);
}
